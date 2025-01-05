package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.Repository.CarRepository;
import com.example.demo.Service.impl.CarServiceImpl;
import com.example.demo.dtos.CarDto;
import com.example.demo.models.Car;

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
    public void testCreateCar_NullCarDto() {
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
}
