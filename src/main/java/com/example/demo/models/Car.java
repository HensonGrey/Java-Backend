package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id                                                 //database primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //database will automatically handle/increment
    private int id;
    private String title;
    private String description;
    private String condition; //new or used
    private int price;
}
