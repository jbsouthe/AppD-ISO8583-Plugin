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

    IReflector setReflector, getStringReflector, getISOHeaderReflector; //ISOMsg

    IReflector getDestinationReflector, getSourceReflector; //ISOHeader

    IReflector awaitUninterruptiblyReflector, isDoneReflector, isCancelledReflector, isSuccessReflector, getCauseReflector; //ChannelFuture

    public ISO8583OutboundInterceptor () {
        getLogger().info(String.format("Initializing ISO8583OutboundInterceptor for ISO8583 Messages"));

        setReflector = makeInvokeInstanceMethodReflector("set", "java.lang.String", "java.lang.String");
        getStringReflector = makeInvokeInstanceMethodReflector("getString", "java.lang.String");
        getISOHeaderReflector = makeInvokeInstanceMethodReflector("getISOHeader");

        getDestinationReflector = makeInvokeInstanceMethodReflector("getDestination");
        getSourceReflector = makeInvokeInstanceMethodReflector("getSource");

        awaitUninterruptiblyReflector = makeInvokeInstanceMethodReflector("awaitUninterruptibly");
        isDoneReflector = makeInvokeInstanceMethodReflector("isDone"); //boolean
        isCancelledReflector = makeInvokeInstanceMethodReflector("isCancelled"); //boolean
        isSuccessReflector = makeInvokeInstanceMethodReflector("isSuccess"); //boolean
        getCauseReflector = makeInvokeInstanceMethodReflector("getCause"); //Throwable
    }


    @Override
    public Object onMethodBegin (java.lang.Object invokedObject, java.lang.String className, java.lang.String methodName, java.lang.Object[] paramValues) {
        Transaction transaction = AppdynamicsAgent.getTransaction();
        if( transaction instanceof NoOpTransaction ) {
            getLogger().info("WARNING, no transaction active, but backend called");
            return null;
        }
        ExitCall exitCall = transaction.startExitCall(getPropertyMap(paramValues[0]), "ISO8583-message", ExitTypes.CUSTOM_ASYNC, true);
        getReflectiveObject(paramValues[0], setReflector, (String) CORRELATION_HEADER_KEY, (String)exitCall.getCorrelationHeader());
        return new State(transaction, exitCall);
    }

    private Map<String, String> getPropertyMap (Object message) {
        Map<String,String> map = new HashMap<>();
        Object isoHeader = getReflectiveObject(message, getISOHeaderReflector);
        map.put("Source", getReflectiveString(isoHeader, getSourceReflector, "Unknown"));
        map.put("Destination", getReflectiveString(isoHeader, getDestinationReflector, "Unknown"));
        map.put("Type", "ISO8583");
        return map;
    }

    @Override
    public void onMethodEnd (java.lang.Object state, java.lang.Object invokedObject, java.lang.String className, java.lang.String methodName, java.lang.Object[] paramValues, java.lang.Throwable thrownException, java.lang.Object returnValue) {
        if( state == null ) return;
        State s = (State) state;
        Object channelFuture = returnValue;
        if( thrownException != null ) {
            s.transaction.markAsError(String.format("Exit Call threw Exception: '%s' ", thrownException.toString()));
        }
        ResponseProcessorThread responseProcessorThread = new ResponseProcessorThread(s.exitCall, s.transaction, channelFuture);
        responseProcessorThread.start();

    }

    @Override
    public List<Rule> initializeRules () {
        List<Rule> rules = new ArrayList<Rule>();

        rules.add(new Rule.Builder(
                "org.jboss.netty.channel.Channel")
                .classMatchType(SDKClassMatchType.IMPLEMENTS_INTERFACE)
                .methodMatchString("write")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build()
        );
        return rules;
    }

    public class ResponseProcessorThread extends Thread {
        private Transaction transaction;
        private ExitCall exitCall;
        private Object channelFuture;
        public ResponseProcessorThread( ExitCall exitCall, Transaction transaction, Object channelFuture ) {
            this.exitCall = exitCall;
            this.transaction = transaction;
            this.channelFuture = channelFuture;
        }

        @Override
        public void run() {
            getReflectiveObject(this.channelFuture, awaitUninterruptiblyReflector);
            boolean isDone = (Boolean) getReflectiveObject(this.channelFuture, isDoneReflector);
            if(!isDone) {
                getLogger().info("WARNING: returned from awaitUninterruptible but isDone not true");
                return;
            }
            boolean isCancelled = (Boolean) getReflectiveObject(this.channelFuture, isCancelledReflector);
            boolean isSuccess = (Boolean) getReflectiveObject(this.channelFuture, isSuccessReflector);
            if( isCancelled ) {
                transaction.markAsError("Request cancelled before completion");
            } else if( !isSuccess ) {
                Throwable throwable = (Throwable) getReflectiveObject(this.channelFuture, getCauseReflector);
                transaction.markAsError(String.format("Request ended in error: %s",throwable.toString()));
            }
            exitCall.end();
        }
    }
}
