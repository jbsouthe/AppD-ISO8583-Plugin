package com.cisco.josouthe;

import static org.junit.jupiter.api.Assertions.*;

class MTIDecoderTest {
    private MTIDecoder mtiDecoder = new MTIDecoder();

    @org.junit.jupiter.api.Test
    void getOrigin () {
        assert( "Acquirer".equals(mtiDecoder.getOrigin("0200")));
    }

    @org.junit.jupiter.api.Test
    void getFunction () {
        assert( "Request".equals(mtiDecoder.getFunction("0200")));
    }

    @org.junit.jupiter.api.Test
    void testGetClass () {
        assert( "Financial".equals(mtiDecoder.getClass("0200")));
    }

    @org.junit.jupiter.api.Test
    void getVersion () {
        assert( "ISO 8583:1987".equals(mtiDecoder.getVersion("0200")));
    }

    @org.junit.jupiter.api.Test
    void getDescription () {
        assert( "Acquirer Financial Request".equals(mtiDecoder.getDescription("0200")));
    }

    @org.junit.jupiter.api.Test
    void getResponseCodeDescription () {
        System.out.println(mtiDecoder.getResponseCodeDescription("1200", "950"));
        assert( "950 violation of business arrangement".equals(mtiDecoder.getResponseCodeDescription("1200", "950")));
        assert( "Pick-up card, special condition".equals(mtiDecoder.getResponseCodeDescription("0200", "7")));
    }
}