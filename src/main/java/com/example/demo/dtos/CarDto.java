package com.example.demo.dtos;

import lombok.Data;

@Data
public class CarDto {
    private String title;
    private String description;
    private String condition; //new or used
    private int price;   
}
