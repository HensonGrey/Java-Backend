package com.example.demo.Service;

import java.util.List;

import com.example.demo.dtos.CarDto;

public interface CarService {
    CarDto createCar(CarDto car); 
    List<CarDto> getAllCars();
    CarDto getCarById(long id);
    void deleteById(long id);
    CarDto updateCarData(long id, CarDto car); 
    List<CarDto> filterCars(Integer minPrice, Integer maxPrice, String condition);
}
