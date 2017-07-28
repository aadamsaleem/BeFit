package com.aadam.befit.helpers;

/**
 * Created by aadam on 14/4/2017.
 */

public class CustomException extends Exception {

    public CustomException(String str) {
        super(str);
    }

    public String getMessage() {
        return super.getMessage();
    }


}
