package com.hamma.f1manager.repository;

import com.hamma.f1manager.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByTeamId(Long teamId);
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}