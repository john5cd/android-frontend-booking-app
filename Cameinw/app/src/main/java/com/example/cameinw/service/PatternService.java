package com.example.cameinw.service;

import java.util.regex.Pattern;

public class PatternService {
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    public static final Pattern PHONE_NUMBER_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +      //any number
                    ".{10}" +               //exactly 10 characters
                    "$");

    public static final Pattern POSTAL_CODE_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +      //any number
                    ".{5}" +               //exactly 10 characters
                    "$");

    public static final Pattern TEXT_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-zA-Z])") ;
}
