package me.adrjan.confjg.example;

import me.adrjan.confjg.ConfjgManager;
import me.adrjan.confjg.example.object.FancyObject;
import me.adrjan.confjg.example.object.FancyObjectAdapter;

public class Example {

    public static void main(String[] args) throws Exception {
        ConfjgManager confjgManager = new ConfjgManager.Builder()
                .addGsonAdapter(FancyObject.class, new FancyObjectAdapter())
                .build();

        TestConfig testConfig = confjgManager
                .registerConfjg(TestConfig.class, true);

        System.out.println(testConfig.getIntegerTest());

        testConfig.setIntegerTest(2222);
        testConfig.save();
        testConfig.reload();
        System.out.println(testConfig.getIntegerTest());
    }
}