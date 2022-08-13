package me.adrjan.confjg.example.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FancyObject {

    private final String name;
    private int age;
}
