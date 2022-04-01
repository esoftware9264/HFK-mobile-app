package com.esoftwere.hfk.utils

import java.util.regex.Pattern

object ValidationHelper {

    /**
     * @Task check for blank text
     */
    fun optionalBlankText(input: String?): String {
        return if (input == null || input.trim { it <= ' ' } == "" || input.isEmpty() || input.trim { it <= ' ' } == "null") {
            ""
        } else {
            input
        }
    }

    /**
     * @Task check if is valid email format
     */
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * @Task check if is valid password format
     */
    fun isValidPasswordFormat(password: String): Boolean {
        val passwordREGEX = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$"
        );
        return passwordREGEX.matcher(password).matches()
    }
}