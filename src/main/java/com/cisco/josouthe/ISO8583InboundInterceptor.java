package com.cisco.josouthe;

import com.appdynamics.agent.api.AppdynamicsAgent;
import com.appdynamics.agent.api.EntryTypes;
import com.appdynamics.instrumentation.sdk.Rule;
import com.appdynamics.instrumentation.sdk.SDKClassMatchType;
import com.appdynamics.instrumentation.sdk.SDKStringMatchType;
import com.appdynamics.instrumentation.sdk.toolbox.reflection.IReflector;

import java.util.ArrayList;
import java.util.List;

public class ISO8583InboundInterceptor extends MyBaseInterceptor {

    IReflector getStringReflector; //ISOMsg

    public ISO8583InboundInterceptor () {
        getLogger().info(String.format("Initializing ISO8583InboundInterceptor for ISO8583 Messages"));

        getStringReflector = makeInvokeInstanceMethodReflector("getString", "java.lang.String");
    }


    @Override
    public Object onMethodBegin (Object invokedObject, String className, String methodName, Object[] paramValues) {
        return null;
    }

    @Override
    public void onMethodEnd (Object state, Object invokedObject, String className, String methodName, Object[] paramValues, Throwable thrownException, Object returnValue) {
        String correlationHeader = (String) getReflectiveObject(returnValue, getStringReflector, CORRELATION_HEADER_KEY);
        AppdynamicsAgent.startTransactionAndServiceEndPoint("ISO8583 Transaction", correlationHeader, "ISO8583 Transaction", EntryTypes.POJO, true);
    }

    @Override
    public List<Rule> initializeRules () {
        List<Rule> rules = new ArrayList<Rule>();

        rules.add(new Rule.Builder(
                "com.bellid.vacq.iso8583.netty.NettyISODecoder")
                .classMatchType(SDKClassMatchType.MATCHES_CLASS)
                .methodMatchString("decode")
                .methodStringMatchType(SDKStringMatchType.EQUALS)
                .build()
        );
        return rules;
    }
}
