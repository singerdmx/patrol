package com.mbrite.patrol.common;

public class BarcodeNotMatchException extends Exception {
    // Parameter less Constructor
    public BarcodeNotMatchException() {
    }

    // Constructor that accepts a message
    public BarcodeNotMatchException(String message) {
        super(message);
    }
}
