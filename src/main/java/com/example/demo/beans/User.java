package com.example.demo.beans;

import lombok.Data;

import java.util.Map;

@Data
public class User {

    private String name;
    private Map<String, Double> wallet;

}
