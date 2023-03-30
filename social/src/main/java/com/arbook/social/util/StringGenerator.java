package com.arbook.social.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;


@Component
public class StringGenerator {
    public String generateString() {
        int length = 8;
        boolean useLetters = true;
        boolean useNumbers = true;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }
}
