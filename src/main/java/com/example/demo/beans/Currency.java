package com.example.demo.beans;

import lombok.Data;

@Data
public class Currency {

    private String code;
    private String alphaCode;
    private String numericCode;
    private String name;
    private double rate;
    private String date;
    private double inverseRate;

}
