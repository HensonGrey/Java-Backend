package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.example.demo.Service.CarService;
import com.example.demo.dtos.CarDto;
import com.example.demo.models.Car;

@RestController
@RequestMapping("/api/")
public class CarController {

    private CarService carService;

    @Autowired
    public CarController(CarService carService){
        this.carService = carService;
    }

    @GetMapping("get-all-cars")
    public ResponseEntity<List<CarDto>> getCars(){
        return new ResponseEntity<>(carService.getAllCars(), HttpStatus.OK);
    }
    
    @GetMapping("car-details/{id}")
    public CarDto getCar(@PathVariable long id){
        return carService.getCarById(id);
    }

    @PostMapping("car/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CarDto> createCar(@RequestBody CarDto car){
        return new ResponseEntity<>(carService.createCar(car), HttpStatus.CREATED);
    }

    @PutMapping("car/update/{id}")
    public ResponseEntity<String> editCar(@PathVariable long id, @RequestBody CarDto car){
        carService.updateCarData(id, car);
        return ResponseEntity.status(HttpStatus.OK).body("Entity data was successfully modified!");
    }

    @DeleteMapping("car/delete/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable long id) {
        carService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Entity was deleted successfully!");
    }
}
