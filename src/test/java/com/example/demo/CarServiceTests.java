package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.Repository.CarRepository;
import com.example.demo.Service.impl.CarServiceImpl;
import com.example.demo.dtos.CarDto;
import com.example.demo.exceptions.CarNotFoundException;
import com.example.demo.models.Car;

import jakarta.persistence.EntityNotFoundException;

public class CarServiceTests {
    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private final String[] finalCarBrands = new String[] { "BMW M3", "AUDI A8" };
    private final String finalDesc = "BEST CAR IN EU DUDe";
    private final String finalCondition = "NEW";
    private final int finalId = 1;
    private final int finalPrice = 20000;

    @Test
    public void testCreateCar_Success() {

        CarDto carDto = new CarDto();
        carDto.setTitle(finalCarBrands[0]);
        carDto.setDescription(finalDesc);
        carDto.setCondition(finalCondition);
        carDto.setPrice(finalPrice);

        Car mappedCar = new Car();
        mappedCar.setTitle(carDto.getTitle());
        mappedCar.setDescription(carDto.getDescription());
        mappedCar.setCondition(carDto.getCondition());
        mappedCar.setPrice(carDto.getPrice());

        Car savedCar = new Car();
        savedCar.setId(finalId);
        savedCar.setTitle(carDto.getTitle());
        savedCar.setDescription(carDto.getDescription());
        savedCar.setCondition(carDto.getCondition());
        savedCar.setPrice(carDto.getPrice());

        when(carRepository.save(any(Car.class))).thenReturn(savedCar);

        CarDto result = carService.createCar(carDto);

        assertNotNull(result);
        assertEquals(savedCar.getTitle(), result.getTitle());
        assertEquals(savedCar.getDescription(), result.getDescription());
        assertEquals(savedCar.getCondition(), result.getCondition());
        assertEquals(savedCar.getPrice(), result.getPrice());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    public void testCreateCar_Failed() {
        CarDto carDto = null;
        Exception exception = assertThrows(NullPointerException.class,
                () -> carService.createCar(carDto));
        assertEquals("This is not a valid car", exception.getMessage());
    }

    @Test
    public void testGetAllCars_Success() {
        List<Car> mockCars = new ArrayList<>();
        Car car1 = new Car();
        car1.setId(finalId);
        car1.setTitle(finalCarBrands[0]);
        car1.setDescription(finalDesc);
        car1.setCondition(finalCondition);
        car1.setPrice(finalPrice);

        Car car2 = new Car();
        car2.setId(2);
        car2.setTitle(finalCarBrands[1]);
        car2.setDescription("Description 2");
        car2.setCondition("Used");
        car2.setPrice(8000);

        mockCars.add(car1);
        mockCars.add(car2);

        when(carRepository.findAll()).thenReturn(mockCars);

        List<CarDto> result = carService.getAllCars();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BMW M3", result.get(0).getTitle());
        assertEquals("AUDI A8", result.get(1).getTitle());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllCars_EmptyList() {
        when(carRepository.findAll()).thenReturn(new ArrayList<>());

        List<CarDto> result = carService.getAllCars();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    public void testGetCarById() {
        Car mockCar = new Car();
        mockCar.setId(finalId);
        mockCar.setTitle(finalCarBrands[0]);

        CarDto expectedCarDto = new CarDto();
        expectedCarDto.setId(finalId);
        expectedCarDto.setTitle(finalCarBrands[0]);

        when(carRepository.getReferenceById((long) finalId)).thenReturn(mockCar);

        carService = spy(carService);
        CarDto result = carService.getCarById(finalId);

        assertNotNull(result);
        assertEquals(expectedCarDto.getId(), result.getId());
        assertEquals(expectedCarDto.getTitle(), result.getTitle());

        verify(carRepository, times(1)).getReferenceById((long) finalId);
    }

    @Test
    public void testDeleteById_Success() {
        long id = (long) finalId;
        Car mockCar = new Car();
        mockCar.setId(finalId);
        mockCar.setTitle(finalCarBrands[0]);

        when(carRepository.findById(id)).thenReturn(Optional.of(mockCar));

        carService.deleteById(id);

        verify(carRepository, times(1)).findById(id);
        verify(carRepository, times(1)).delete(mockCar);
    }

    @Test
    public void testDeleteById_Failed() {
        long id = (long) finalId;
        when(carRepository.findById(id)).thenReturn(Optional.empty());

        CarNotFoundException exception = assertThrows(CarNotFoundException.class, () -> {
            carService.deleteById(finalId);
        });

        assertEquals("Could not find car with id:" + finalId, exception.getMessage());
        verify(carRepository, times(1)).findById(id);
        verify(carRepository, never()).delete(any(Car.class));
    }

    @Test
    public void testUpdateCarData_Success() {
        long id = (long) finalId;

        Car existingCar = new Car();
        existingCar.setId(finalId);
        existingCar.setTitle(finalCarBrands[0]);
        existingCar.setDescription(finalDesc);
        existingCar.setCondition(finalCondition);
        existingCar.setPrice(finalPrice);

        CarDto updatedCarDto = new CarDto();
        updatedCarDto.setTitle(finalCarBrands[1]);
        updatedCarDto.setDescription("I'm the better car");
        updatedCarDto.setCondition("Used");
        updatedCarDto.setPrice(400);

        when(carRepository.findById(id)).thenReturn(Optional.of(existingCar));

        Car savedCar = new Car();
        savedCar.setId(finalId);
        savedCar.setTitle(updatedCarDto.getTitle());
        savedCar.setDescription(updatedCarDto.getDescription());
        savedCar.setCondition(updatedCarDto.getCondition());
        savedCar.setPrice(updatedCarDto.getPrice());

        when(carRepository.save(existingCar)).thenReturn(savedCar);

        CarDto expectedCarDto = new CarDto();
        expectedCarDto.setTitle(savedCar.getTitle());
        expectedCarDto.setDescription(savedCar.getDescription());
        expectedCarDto.setCondition(savedCar.getCondition());
        expectedCarDto.setPrice(savedCar.getPrice());

        CarDto result = carService.updateCarData(id, updatedCarDto);

        assertNotNull(result);
        assertEquals("AUDI A8", result.getTitle());
        assertEquals("I'm the better car", result.getDescription());
        assertEquals("Used", result.getCondition());
        assertEquals(400, result.getPrice());

        verify(carRepository, times(1)).findById(id);
        verify(carRepository, times(1)).save(existingCar);
    }

    @Test
    public void testUpdateCarData_EntityNotFound() {
        long id = (long) finalId;
        CarDto carDto = new CarDto();

        when(carRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            carService.updateCarData(id, carDto);
        });

        assertEquals("Car with ID " + id + " not found", exception.getMessage());
        verify(carRepository, times(1)).findById(id);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    public void testFilterCars_Success() {
        Integer minPrice = 1000;
        Integer maxPrice = 10000;
        String condition = "New";

        Car car1 = new Car();
        car1.setId(finalId);
        car1.setTitle("Car 1");
        car1.setPrice(5000);
        car1.setCondition("New");

        Car car2 = new Car();
        car2.setId(2);
        car2.setTitle("Car 2");
        car2.setPrice(8000);
        car2.setCondition("New");

        List<Car> filteredCars = List.of(car1, car2);

        when(carRepository.findFilteredCars(minPrice, maxPrice, condition)).thenReturn(filteredCars);

        List<CarDto> result = carService.filterCars(minPrice, maxPrice, condition);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Car 1", result.get(0).getTitle());
        assertEquals("Car 2", result.get(1).getTitle());

        verify(carRepository, times(1)).findFilteredCars(minPrice, maxPrice, condition);
    }
}
