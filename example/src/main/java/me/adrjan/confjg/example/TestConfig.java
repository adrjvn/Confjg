package me.adrjan.confjg.example;

import lombok.Getter;
import lombok.Setter;
import me.adrjan.confjg.Confjg;
import me.adrjan.confjg.ConfjgInfo;
import me.adrjan.confjg.example.object.FancyObject;

import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@ConfjgInfo(name = "ConfigFile", path = "example/src/main/resources/configuration")
public class TestConfig extends Confjg {

    private String str = "ssssss";
    private int integerTest = 2137;
    private List<String> list = Arrays.asList("test", "dasfgsdgsd", "saddd");
    private List<FancyObject> objects = Arrays.asList(
            new FancyObject("test", 22),
            new FancyObject("oo", 1337));
}