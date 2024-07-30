package com.cisco.josouthe;

import com.appdynamics.agent.api.AppdynamicsAgent;
import com.appdynamics.agent.api.EntryTypes;
import com.appdynamics.agent.api.Transaction;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;

import java.util.ArrayList;
import java.util.List;

public class ISO8583InboundInterceptor extends MyBaseInterceptor {

    private MTIDecoder mtiDecoder = new MTIDecoder();

    IReflector setReflector, getStringReflector, getISOHeaderReflector, getMTIReflector, getValueReflector; //ISOMsg

    IReflector getDestinationReflector, getSourceReflector; //ISOHeader

    public ISO8583InboundInterceptor () {
        getLogger().info(String.format("Initializing ISO8583InboundInterceptor for ISO8583 Messages"));

        setReflector = makeInvokeInstanceMethodReflector("set", "java.lang.String", "java.lang.String");
        getStringReflector = makeInvokeInstanceMethodReflector("getString", "java.lang.String");
        getISOHeaderReflector = makeInvokeInstanceMethodReflector("getISOHeader");
        getMTIReflector = makeInvokeInstanceMethodReflector("getMTI");
        getValueReflector = makeInvokeInstanceMethodReflector("getValue", "java.lang.String");

        getDestinationReflector = makeInvokeInstanceMethodReflector("getDestination");
        getSourceReflector = makeInvokeInstanceMethodReflector("getSource");
    }


    @Override
    public Object onMethodBegin (Object invokedObject, String className, String methodName, Object[] paramValues){
        Object iosMsg = paramValues[0];
        Object iMessageSource = paramValues[1];

        String mti = (String) getReflectiveObject(iosMsg, getMTIReflector);
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

        String correlationHeader = null;
        if( "true".equalsIgnoreCase(getProperty(ISO8583_CORRELATION_ENABLED)))
            correlationHeader = (String) getReflectiveObject(iosMsg, getStringReflector, (String) getProperty(ISO8583_CORRELATION_FIELD));
        String btName = String.format("ISO8583 %s Transaction",mtiClass);
        Transaction transaction = AppdynamicsAgent.startTransactionAndServiceEndPoint(btName, correlationHeader, btName, EntryTypes.POJO, true);
        transaction.collectData("ISO8583_Origin", mtiOrigin, this.dataScopes);
        transaction.collectData("ISO8583_Function", mtiFunction, this.dataScopes);
        transaction.collectData("ISO8583_Version", mtiVersion, this.dataScopes);
        transaction.collectData("ISO8583_Class", mtiClass, this.dataScopes);
        transaction.collectData("ISO8583_Description", mtiDecoder.getDescription(mti), this.dataScopes);
        transaction.collectData("ISO8583_Transaction_Amount", (String) getReflectiveObject(iosMsg, getStringReflector, "4"), this.dataScopes);
        return transaction;
    }

    @Override
    public void onMethodEnd (Object state, Object invokedObject, String className, String methodName, Object[] paramValues, Throwable thrownException, Object returnValue) {
        Transaction transaction = (Transaction) state;
        transaction.end();
    }

    @Override
    public List<Rule> initializeRules () {
        List<Rule> rules = new ArrayList<Rule>();

        rules.add(new Rule.Builder(
                "com.bellid.vacq.iso8583.common.ISOProcessor")
                .classMatchType(SDKClassMatchType.IMPLEMENTS_INTERFACE)
                .methodMatchString("process")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build()
        );
        return rules;
    }
}
