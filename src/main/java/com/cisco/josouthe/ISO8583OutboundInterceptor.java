package com.cisco.josouthe;

import com.appdynamics.agent.api.AppdynamicsAgent;
import com.appdynamics.agent.api.ExitCall;
import com.appdynamics.agent.api.ExitTypes;
import com.appdynamics.agent.api.Transaction;
import com.appdynamics.agent.api.impl.NoOpTransaction;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ISO8583OutboundInterceptor extends MyBaseInterceptor {

    private MTIDecoder mtiDecoder = new MTIDecoder();
    IReflector setReflector, getStringReflector, getISOHeaderReflector, getMTIReflector, getValueReflector; //ISOMsg

    IReflector getDestinationReflector, getSourceReflector; //ISOHeader

    IReflector getRemoteAddressReflector, getLocalAddressReflector; //Channel

    IReflector getInetAddressReflector; //SocketAddress

    IReflector getCanonicalHostNameReflector; //InetAddress

    public ISO8583OutboundInterceptor () {
        getLogger().info(String.format("Initializing ISO8583OutboundInterceptor for ISO8583 Messages"));

        setReflector = makeInvokeInstanceMethodReflector("set", "java.lang.String", "java.lang.String");
        getStringReflector = makeInvokeInstanceMethodReflector("getString", "java.lang.String");
        getISOHeaderReflector = makeInvokeInstanceMethodReflector("getISOHeader");
        getMTIReflector = makeInvokeInstanceMethodReflector("getMTI");
        getValueReflector = makeInvokeInstanceMethodReflector("getValue", "java.lang.String");

        getDestinationReflector = makeInvokeInstanceMethodReflector("getDestination");
        getSourceReflector = makeInvokeInstanceMethodReflector("getSource");

        getRemoteAddressReflector = makeInvokeInstanceMethodReflector("getRemoteAddress"); //SocketAddress
        getLocalAddressReflector = makeInvokeInstanceMethodReflector("getLocalAddress"); //SocketAddress

        getInetAddressReflector = makeInvokeInstanceMethodReflector("getInetAddress");

        getCanonicalHostNameReflector = makeInvokeInstanceMethodReflector("getCanonicalHostName");
    }


    @Override
    public Object onMethodBegin (java.lang.Object invokedObject, java.lang.String className, java.lang.String methodName, java.lang.Object[] paramValues) {
        Transaction transaction = AppdynamicsAgent.getTransaction();
        if( transaction instanceof NoOpTransaction ) {
            //transaction = AppdynamicsAgent.startTransaction("ISO8583-Placeholder", null, EntryTypes.POJO, true);
            getLogger().info("WARNING, no transaction active, but backend called");
            return null;
        }
        Object possibleMessage = paramValues[2];
        String mti = (String) getReflectiveObject(possibleMessage, getMTIReflector);
        String mtiClass = "Unknown";
        String mtiVersion = "Unknown";
        String mtiFunction = "Unknown";
        String mtiOrigin = "Unknown";
        if( mti != null ) {
            mtiClass = mtiDecoder.getClass(mti);
            mtiVersion = mtiDecoder.getVersion(mti);
            mtiFunction = mtiDecoder.getFunction(mti);
            mtiOrigin = mtiDecoder.getOrigin(mti);
        }
        getLogger().debug(String.format("MTI: %s Class: %s Version: %s Function: %s Origin: %s", mti, mtiClass, mtiVersion, mtiFunction, mtiOrigin));

        Map<String, String> propertyMap = new HashMap<>();
        Object isoHeader = getReflectiveObject(possibleMessage, getISOHeaderReflector);
        propertyMap.put("Source", getHostname(getReflectiveObject(isoHeader, getSourceReflector), getReflectiveObject(paramValues[1], getLocalAddressReflector) ));
        propertyMap.put("Destination", getHostname(getReflectiveObject(isoHeader, getDestinationReflector), getReflectiveObject(paramValues[1], getRemoteAddressReflector) ));
        propertyMap.put("Type", "ISO8583");
        propertyMap.put("Class", mtiClass);

        ExitCall exitCall = transaction.startExitCall( propertyMap, String.format("ISO8583-%s-message", mtiClass), ExitTypes.CUSTOM_ASYNC, true);
        if( "true".equalsIgnoreCase(getProperty(ISO8583_CORRELATION_ENABLED)))
            getReflectiveObject(paramValues[0], setReflector, (String) getProperty(ISO8583_CORRELATION_FIELD), (String)exitCall.getCorrelationHeader());
        transaction.collectData("ISO8583_Origin", mtiOrigin, this.dataScopes);
        transaction.collectData("ISO8583_Function", mtiFunction, this.dataScopes);
        transaction.collectData("ISO8583_Version", mtiVersion, this.dataScopes);
        transaction.collectData("ISO8583_Class", mtiClass, this.dataScopes);
        transaction.collectData("ISO8583_Description", mtiDecoder.getDescription(mti), this.dataScopes);
        transaction.collectData("ISO8583_Transaction_Amount", (String) getReflectiveObject(possibleMessage, getStringReflector, "4"), this.dataScopes);
        transaction.collectData("ISO8583_Source", propertyMap.get("Source"), this.dataScopes);
        transaction.collectData("ISO8583_Destination", propertyMap.get("Destination"), this.dataScopes);
        return new State(transaction, exitCall);
    }

    private String getHostname (Object isoHeaderValue, Object socketAddress) {
        if( isoHeaderValue != null ) return String.valueOf(isoHeaderValue);
        if( socketAddress != null ) {
            Object inetAddress = getReflectiveObject(socketAddress, getInetAddressReflector);
            return getReflectiveString(inetAddress, getCanonicalHostNameReflector, "Unknown");
        }
        return "Unknown";
    }

    @Override
    public void onMethodEnd (java.lang.Object state, java.lang.Object invokedObject, java.lang.String className, java.lang.String methodName, java.lang.Object[] paramValues, java.lang.Throwable thrownException, java.lang.Object returnValue) {
        if( state == null ) return;
        State s = (State) state;
        Object channelFuture = returnValue;
        if( thrownException != null ) {
            s.transaction.markAsError(String.format("Exit Call threw Exception: '%s' ", thrownException.toString()));
        }
        s.exitCall.end();
    }

    @Override
    public List<Rule> initializeRules () {
        List<Rule> rules = new ArrayList<Rule>();

        rules.add(new Rule.Builder(
                "com.bellid.vacq.iso8583.netty.NettyISOEncoder")
                .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                .methodMatchString("encode")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build()
        );
        return rules;
    }

}
