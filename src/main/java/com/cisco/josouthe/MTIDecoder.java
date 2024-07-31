package com.cisco.josouthe;

import java.util.HashMap;
import java.util.Map;

public class MTIDecoder { //https://en.wikipedia.org/wiki/ISO_8583
    private Map<String,String> mtiMap, versionMap, classMap, functionMap, originMap, responseCode1987Map, responseCode1993Map;

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

        this.responseCode1987Map = new HashMap<>();
        responseCode1987Map.put("0", "Approved or completed successfully");
        responseCode1987Map.put("1", "Refer to card issuer");
        responseCode1987Map.put("2", "Refer to card issuer, special condition");
        responseCode1987Map.put("3", "Invalid merchant");
        responseCode1987Map.put("4", "Pick-up card");
        responseCode1987Map.put("5", "Do not honor");
        responseCode1987Map.put("6", "Error");
        responseCode1987Map.put("7", "Pick-up card, special condition");
        responseCode1987Map.put("8", "Honor with identification");
        responseCode1987Map.put("9", "Request in progress");
        responseCode1987Map.put("10", "Approved, partial");
        responseCode1987Map.put("11", "Approved, VIP");
        responseCode1987Map.put("12", "Invalid transaction");
        responseCode1987Map.put("13", "Invalid amount");
        responseCode1987Map.put("14", "Invalid card number");
        responseCode1987Map.put("15", "No such issuer");
        responseCode1987Map.put("16", "Approved, update track 3");
        responseCode1987Map.put("17", "Customer cancellation");
        responseCode1987Map.put("18", "Customer dispute");
        responseCode1987Map.put("19", "Re-enter transaction");
        responseCode1987Map.put("20", "Invalid response");
        responseCode1987Map.put("21", "No action taken");
        responseCode1987Map.put("22", "Suspected malfunction");
        responseCode1987Map.put("23", "Unqualified approval");
        responseCode1987Map.put("24", "No such card");
        responseCode1987Map.put("25", "No such issuer");
        responseCode1987Map.put("26", "Duplicate transaction");
        responseCode1987Map.put("27", "Invalid account number");
        responseCode1987Map.put("28", "File update error");
        responseCode1987Map.put("29", "Unknown error");
        responseCode1987Map.put("30", "Format error");
        responseCode1987Map.put("31", "Duplicate request");
        responseCode1987Map.put("32", "Error in request");
        responseCode1987Map.put("33", "Transaction not allowed");
        responseCode1987Map.put("34", "No such card");
        responseCode1987Map.put("35", "Card expired");
        responseCode1987Map.put("36", "Card not found");
        responseCode1987Map.put("37", "Invalid transaction");
        responseCode1987Map.put("38", "Invalid card");
        responseCode1987Map.put("39", "Transaction canceled");
        responseCode1987Map.put("40", "Invalid response code");
        responseCode1987Map.put("41", "Card expired");
        responseCode1987Map.put("42", "Issuer not available");
        responseCode1987Map.put("43", "Card not allowed");
        responseCode1987Map.put("44", "Amount exceeds limit");
        responseCode1987Map.put("45", "Invalid transaction");
        responseCode1987Map.put("46", "Insufficient funds");
        responseCode1987Map.put("47", "Account closed");
        responseCode1987Map.put("48", "Account frozen");
        responseCode1987Map.put("49", "Invalid PIN");
        responseCode1987Map.put("50", "Invalid transaction");
        responseCode1987Map.put("51", "Invalid amount");
        responseCode1987Map.put("52", "Card not found");
        responseCode1987Map.put("53", "Invalid card number");
        responseCode1987Map.put("54", "No such account");
        responseCode1987Map.put("55", "Invalid date");
        responseCode1987Map.put("56", "Invalid PIN");
        responseCode1987Map.put("57", "Invalid transaction");
        responseCode1987Map.put("58", "Invalid amount");
        responseCode1987Map.put("59", "Invalid card number");
        responseCode1987Map.put("60", "Card expired");
        responseCode1987Map.put("61", "Invalid card");
        responseCode1987Map.put("62", "Invalid PIN");
        responseCode1987Map.put("63", "Invalid amount");
        responseCode1987Map.put("64", "Card expired");
        responseCode1987Map.put("65", "Invalid transaction");
        responseCode1987Map.put("66", "Invalid card");
        responseCode1987Map.put("67", "Card not allowed");
        responseCode1987Map.put("68", "Card not found");
        responseCode1987Map.put("69", "Invalid amount");
        responseCode1987Map.put("70", "Invalid date");
        responseCode1987Map.put("71", "Invalid PIN");
        responseCode1987Map.put("72", "Invalid card number");
        responseCode1987Map.put("73", "Invalid transaction");
        responseCode1987Map.put("74", "Invalid amount");
        responseCode1987Map.put("75", "Card expired");
        responseCode1987Map.put("76", "Invalid PIN");
        responseCode1987Map.put("77", "Card not found");
        responseCode1987Map.put("78", "Invalid transaction");
        responseCode1987Map.put("79", "Invalid amount");
        responseCode1987Map.put("80", "Card not allowed");
        responseCode1987Map.put("81", "Invalid date");
        responseCode1987Map.put("82", "Card expired");
        responseCode1987Map.put("83", "Invalid PIN");
        responseCode1987Map.put("84", "Invalid amount");
        responseCode1987Map.put("85", "Invalid transaction");
        responseCode1987Map.put("86", "Card not found");
        responseCode1987Map.put("87", "Card not allowed");
        responseCode1987Map.put("88", "Invalid card number");
        responseCode1987Map.put("89", "Invalid date");
        responseCode1987Map.put("90", "Invalid PIN");
        responseCode1987Map.put("91", "Card expired");
        responseCode1987Map.put("92", "Invalid transaction");
        responseCode1987Map.put("93", "Card not found");
        responseCode1987Map.put("94", "Card not allowed");
        responseCode1987Map.put("95", "Invalid amount");
        responseCode1987Map.put("96", "Invalid date");
        responseCode1987Map.put("97", "Card expired");
        responseCode1987Map.put("98", "Invalid PIN");
        responseCode1987Map.put("99", "Invalid transaction");
    }

    public String getOrigin( String mti ) {
        String value = originMap.get(mti.substring(3));
        if( value == null ) return "Unknown";
        return value;
    }

    public String getFunction( String mti ) {
        String value = functionMap.get( String.valueOf(mti.charAt(2)) );
        if( value == null ) return "Unknown";
        return value;
    }

    public String getClass( String mti ) {
        String value = classMap.get( String.valueOf(mti.charAt(1)) );
        if( value == null ) return "Unknown";
        return value;
    }

    public String getVersion( String mti ) {
        String value = versionMap.get( String.valueOf(mti.charAt(0)) );
        if( value == null ) return "Unknown";
        return value;
    }

    public String getDescription( String mti ) {
        String value = mtiMap.get(mti);
        if( value == null ) return mti;
        return value;
    }

    public String getProcessingCodeDescription (String mti, String processingCode) { //https://en.wikipedia.org/wiki/ISO_8583#Processing_code
        switch (getClass(mti)) {
            case "Authorization": {
                if( "16".equals(processingCode) ) return "Authorization"; //00 a0 0x
                if( "12688".equals(processingCode) ) return "Balance Inquiry"; //31 a0 0x
                break;
            }
            case "Financial": {
                if( "16".equals(processingCode) ) return "Sale"; //00 a0 0x
                if( "416".equals(processingCode) ) return "Cash"; //01 a0 0x
                if( "8352".equals(processingCode) ) return "Credit Voucher"; //20 a0 0x
                if( "672".equals(processingCode) ) return "Void"; //02 a0 0x
                break;
            }
            default: {
                if( "22312".equals(processingCode) ) return "Mobile Topup"; //57 a0 0x
                break;
            }
        }
        return null;
    }

    public String getResponseCodeDescription (String mti, String responseCode) {
        if( "ISO 8583:1987".equals(getVersion(mti)) ) return responseCode1987Map.get(responseCode);
        try {
            int responseCodeInt = Integer.parseInt(responseCode);
            if( responseCodeInt < 100 ) return responseCode +" Approved";
            if( responseCodeInt < 200 ) return responseCode +" Denied";
            if( responseCodeInt < 300 ) return responseCode +" Denied, pick up card";
            if( responseCodeInt == 300 ) return responseCode +" File Action Success";
            if( responseCodeInt < 400 ) return responseCode +" File Action Not Successful";
            if( responseCodeInt == 400 ) return responseCode +" Reversal Accepted";
            if( responseCodeInt == 500 ) return responseCode +" Reconciled, in balance";
            if( responseCodeInt == 501 ) return responseCode +" Reconciled, out of balance";
            if( responseCodeInt == 502 ) return responseCode +" amount not reconciled, totals provided";
            if( responseCodeInt == 503 ) return responseCode +" totals not available";
            if( responseCodeInt == 504 ) return responseCode +" not reconciled, totals provided";
            if( responseCodeInt == 600 ) return responseCode +" Accepted";
            if( responseCodeInt == 601 ) return responseCode +" not able to trace back original transaction";
            if( responseCodeInt == 602 ) return responseCode +" invalid reference number";
            if( responseCodeInt == 603 ) return responseCode +" reference number/PAN incompatible";
            if( responseCodeInt == 604 ) return responseCode +" POS photograph is not available";
            if( responseCodeInt == 605 ) return responseCode +" item supplied";
            if( responseCodeInt == 606 ) return responseCode +" request cannot be fulfilled - required/requested documentation is not available";
            if( responseCodeInt == 700 ) return responseCode +" Accepted";
            if( responseCodeInt == 800 ) return responseCode +" Accepted";
            if( responseCodeInt == 900 ) return responseCode +" advice acknowledged, no financial liability accepted";
            if( responseCodeInt == 901 ) return responseCode +" advice acknowledged, financial liability accepted";
            if (responseCodeInt == 902) return responseCode + " invalid transaction";
            if (responseCodeInt == 903) return responseCode + " re-enter transaction";
            if (responseCodeInt == 904) return responseCode + " format error";
            if (responseCodeInt == 905) return responseCode + " acquirer not supported by switch";
            if (responseCodeInt == 906) return responseCode + " cutover in process";
            if (responseCodeInt == 907) return responseCode + " card issuer or switch inoperative";
            if (responseCodeInt == 908) return responseCode + " transaction destination cannot be found for routing";
            if (responseCodeInt == 909) return responseCode + " system malfunction";
            if (responseCodeInt == 910) return responseCode + " card issuer signed off";
            if (responseCodeInt == 911) return responseCode + " card issuer timed out";
            if (responseCodeInt == 912) return responseCode + " card issuer unavailable";
            if (responseCodeInt == 913) return responseCode + " duplicate transmission";
            if (responseCodeInt == 914) return responseCode + " not able to trace back to original transaction";
            if (responseCodeInt == 915) return responseCode + " reconciliation cutover or checkpoint error";
            if (responseCodeInt == 916) return responseCode + " MAC incorrect";
            if (responseCodeInt == 917) return responseCode + " MAC key sync error";
            if (responseCodeInt == 918) return responseCode + " No communication keys available for use";
            if (responseCodeInt == 919) return responseCode + " encryption key sync error";
            if (responseCodeInt == 920) return responseCode + " security software/hardware error - try again";
            if (responseCodeInt == 921) return responseCode + " security software/hardware error - no action";
            if (responseCodeInt == 922) return responseCode + " message number out of sequence";
            if (responseCodeInt == 923) return responseCode + " request in progress";
            if (responseCodeInt == 950) return responseCode + " violation of business arrangement";
        } catch (Exception exception) {
            //noop
        }
        return responseCode +" Unknown";
    }
}
