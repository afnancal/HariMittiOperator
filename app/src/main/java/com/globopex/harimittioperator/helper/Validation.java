package com.globopex.harimittioperator.helper;

import android.text.TextUtils;

/**
 * Created by Afnan on 22/2/16.
 */
public class Validation {

    /**
     * Validation of Email Address
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Validation of Phone Number
     */
    public final static boolean isValidPhoneNumber(CharSequence target) {
        int firstLetter = Integer.parseInt(Character.toString(target.charAt(0)));
        if (target.length() < 10) {
            return false;
        } else if (firstLetter == 7 || firstLetter == 8 || firstLetter == 9) {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
        return false;
    }


    /**
     * Validation of Pincode
     */
    public final static boolean isValidPincode(CharSequence target) {
        if (target.length() < 6) {
            return false;
        } else
            return true;
    }

    /**
     * Validation of Pincode
     */
    public final static boolean isValidOTP(CharSequence target) {
        if (target.length() < 6) {
            return false;
        } else
            return true;
    }

}
