package com.cisco.josouthe;

import java.util.HashMap;
import java.util.Map;

public class MTIDecoder { //https://en.wikipedia.org/wiki/ISO_8583
    private Map<String,String> mtiMap, versionMap, classMap, functionMap, originMap;

    public MTIDecoder () {
        this.mtiMap = new HashMap<>();
        this.mtiMap.put("0100","Authorization Request");
        this.mtiMap.put("0110","Authorization Response");
        this.mtiMap.put("0120","Authorization Advice");
        this.mtiMap.put("0121","Authorization Advice Repeat");
        this.mtiMap.put("0130","Issuer Response to Authorization Advice");
        this.mtiMap.put("0200","Acquirer Financial Request");
        this.mtiMap.put("0210","Issuer Response to Financial Request");
        this.mtiMap.put("0220","Acquirer Financial Advice");
        this.mtiMap.put("0221","Acquirer Financial Advice Repeat");
        this.mtiMap.put("0230","Issuer Response to Financial Advice");
        this.mtiMap.put("0320","Batch Upload");
        this.mtiMap.put("0330","Batch Upload Response");
        this.mtiMap.put("0400","Acquirer Reversal Request");
        this.mtiMap.put("0420","Acquirer Reversal Advice");
        this.mtiMap.put("0430","Acquirer Reversal Advice Response");
        this.mtiMap.put("0510","Batch Settlement Response");
        this.mtiMap.put("0800","Network Management Request");
        this.mtiMap.put("0810","Network Management Response");
        this.mtiMap.put("0820","Network Management Advice");

        this.versionMap = new HashMap<>();
        this.versionMap.put("0","ISO 8583:1987");
        this.versionMap.put("1","ISO 8583:1993");
        this.versionMap.put("2","ISO 8583:2003");
        this.versionMap.put("3","Reserved");
        this.versionMap.put("4","Reserved");
        this.versionMap.put("5","Reserved");
        this.versionMap.put("6","Reserved");
        this.versionMap.put("7","Reserved");
        this.versionMap.put("8","National Use");
        this.versionMap.put("9","Private Use");

        this.classMap = new HashMap<>();
        this.classMap.put("0","Reserved");
        this.classMap.put("1","Authorization");
        this.classMap.put("2","Financial");
        this.classMap.put("3","File Action");
        this.classMap.put("4","Reversal And Charge Back");
        this.classMap.put("5","Reconciliation");
        this.classMap.put("6","Administrative");
        this.classMap.put("7","Fee Collection");
        this.classMap.put("8","Network Management");
        this.classMap.put("9","Reserved");

        this.functionMap = new HashMap<>();
        this.functionMap.put("0","Request");
        this.functionMap.put("1","Request Response");
        this.functionMap.put("2","Advice");
        this.functionMap.put("3","Advice Response");
        this.functionMap.put("4","Notification");
        this.functionMap.put("5","Notification Ack");
        this.functionMap.put("6","Instruction");
        this.functionMap.put("7","Instruction Ack");
        this.functionMap.put("8","Reserved");
        this.functionMap.put("9","Reserved");

        this.originMap = new HashMap<>();
        this.originMap.put("0","Acquirer");
        this.originMap.put("1","Acquirer Repeat");
        this.originMap.put("2","Issuer");
        this.originMap.put("3","Issuer Repeat");
        this.originMap.put("4","Other");
        this.originMap.put("60","Reserved");
        this.originMap.put("6","Reserved");
        this.originMap.put("41","Reserved");

    }

    public String getOrigin( String mti ) {
        String value = originMap.get(mti.substring(3));
        if( value == null ) return "Unknown";
        return value;
    }

    public String getFunction( String mti ) {
        String value = functionMap.get( mti.charAt(2) );
        if( value == null ) return "Unknown";
        return value;
    }

    public String getClass( String mti ) {
        String value = classMap.get( mti.charAt(1) );
        if( value == null ) return "Unknown";
        return value;
    }

    public String getVersion( String mti ) {
        String value = versionMap.get( mti.charAt(0) );
        if( value == null ) return "Unknown";
        return value;
    }

    public String getDescription( String mti ) {
        String value = mtiMap.get(mti);
        if( value == null ) return mti;
        return value;
    }
}
