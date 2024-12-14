package com.example.demo.Service.impl;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.CarRepository;
import com.example.demo.Service.CarService;
import com.example.demo.dtos.CarDto;
import com.example.demo.exceptions.CarNotFoundException;
import com.example.demo.models.Car;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CarServiceImpl implements CarService {

    private CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository){
        this.carRepository = carRepository;
    }

    @Override
    public CarDto createCar(CarDto car) {
        Car mappedCar = new Car();

        mappedCar.setTitle(car.getTitle());
        mappedCar.setDescription(car.getDescription());
        mappedCar.setCondition(car.getCondition());
        mappedCar.setPrice(car.getPrice());

        Car newCar = carRepository.save(mappedCar);
        return MapToDto(newCar);
    }

    @Override
    public List<CarDto> getAllCars() {
        List<Car> allCars = carRepository.findAll();
        return allCars.stream().map(car -> MapToDto(car)).collect(Collectors.toList());
    }
    
    private CarDto MapToDto(Car car){
        CarDto mappedCar = new CarDto();
        mappedCar.setTitle(car.getTitle());
        mappedCar.setDescription(car.getDescription());
        mappedCar.setCondition(car.getCondition());
        mappedCar.setPrice(car.getPrice());
        return mappedCar;
    }

    @Override
    public CarDto getCarById(long id) {
        return MapToDto(carRepository.getReferenceById(id));
    }

    @Override
    public void deleteById(long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException("Could not find car with id:" + id));
        carRepository.delete(car);
    }

    @Override
    public CarDto updateCarData(long id, CarDto carDto) {
        Car car = carRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Car with ID " + id + " not found"));

        car.setTitle(carDto.getTitle());
        car.setDescription(carDto.getDescription());
        car.setCondition(carDto.getCondition());
        car.setPrice(carDto.getPrice());

        Car newCar = carRepository.save(car);

        return MapToDto(newCar);
    }

    @Override
    public List<CarDto> filterCars(Integer minPrice, Integer maxPrice, String condition) {
        List<Car> cars = carRepository.findFilteredCars(minPrice, maxPrice, condition);

        List<CarDto> mapped_to_dto = cars.stream().map(this::MapToDto).collect(Collectors.toList());
        return mapped_to_dto;
    }
}
