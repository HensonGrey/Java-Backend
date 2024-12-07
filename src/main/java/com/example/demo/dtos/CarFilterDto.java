package com.example.demo.dtos;

import lombok.Data;

@Data
public class CarFilterDto {
    private Integer minPrice;
    private Integer maxPrice;
    private String condition;
}
