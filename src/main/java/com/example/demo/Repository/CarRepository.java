package com.example.demo.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
    @Query("SELECT c FROM Car c WHERE " +
           "(:minPrice IS NULL OR c.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR c.price <= :maxPrice) AND " +
           "(:condition IS NULL OR c.condition = :condition)")
    List<Car> findFilteredCars(@Param("minPrice") Integer minPrice, 
                               @Param("maxPrice") Integer maxPrice, 
                               @Param("condition") String condition);
}
