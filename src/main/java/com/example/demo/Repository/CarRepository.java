package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
    
}
