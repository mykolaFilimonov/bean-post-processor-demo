package com.example.beanpostprocessordemo.postprocessor.service;

import com.example.beanpostprocessordemo.annotation.Multiply;
import com.example.beanpostprocessordemo.annotation.Trimmed;
import org.springframework.stereotype.Service;

@Service
public class TrimmedService {

    public String getTheTrimmedString(@Trimmed String string) {
        return string;
    }

    public String getTheTrimmedInteger(@Trimmed Integer num) {
        return num.toString();
    }
    public String getTheTrimmedInteger2(@Trimmed String string, @Multiply Integer num) {
        return num.toString().concat(string);
    }

    public String getTheTrimmedStringWithTwoArgs(@Trimmed String stringOne, String stringTwo) {
        return stringOne.concat(stringTwo);
    }

    public String getTheTrimmedStringWithThreeArgs(@Trimmed String stringOne, String stringTwo, @Trimmed String stringThree) {
        return stringOne.concat(stringTwo).concat(stringThree);
    }

    public String getNotTrimmedString(String string) {
        return string;
    }
}
