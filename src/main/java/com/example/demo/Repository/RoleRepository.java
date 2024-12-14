package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Roles;

public interface RoleRepository extends JpaRepository<Roles, Integer>{
    Optional<Roles> findByName(String name);
}
