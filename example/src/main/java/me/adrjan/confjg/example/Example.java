package me.adrjan.confjg.example;

import me.adrjan.confjg.ConfjgManager;

public class Example {

    public static void main(String[] args) throws Exception {
        ConfjgManager confjgManager = ConfjgManager.Builder.build();

        TestConfig testConfig = confjgManager
                .registerConfjg(TestConfig.class);

        System.out.println(testConfig.getIntegerTest());
        testConfig.reload();
        testConfig.save();
    }
}