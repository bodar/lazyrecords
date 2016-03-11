package com.googlecode.lazyrecords.simpledb;

import com.amazonaws.auth.AWSCredentials;
import com.googlecode.totallylazy.Strings;

import java.util.Map;

public class AwsEnvironmentCredentials implements AWSCredentials {
    public static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    public static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    private final String accessKey;
    private final String secretKey;

    public AwsEnvironmentCredentials(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public static AwsEnvironmentCredentials awsCredentials(Map<String, String> map) {
        return new AwsEnvironmentCredentials(get(map, AWS_ACCESS_KEY_ID), get(map, AWS_SECRET_ACCESS_KEY));
    }

    private static String get(Map<String, String> map, String key) {
        String value = map.get(key);
        if(Strings.isEmpty(value)) throw new IllegalArgumentException(key + " was not set");
        return value;
    }

    public static AwsEnvironmentCredentials awsCredentials() {
        return awsCredentials(System.getenv());
    }

    @Override
    public String getAWSAccessKeyId() {
        return accessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return secretKey;
    }
}
