package com.hamma.f1manager.service;

import com.hamma.f1manager.dto.DriverDTO;
import com.hamma.f1manager.exception.DuplicateResourceException;
import com.hamma.f1manager.exception.ResourceNotFoundException;
import com.hamma.f1manager.model.Driver;
import com.hamma.f1manager.model.Team;
import com.hamma.f1manager.repository.DriverRepository;
import com.hamma.f1manager.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;
    private final TeamRepository teamRepository;

    public List<DriverDTO> findAll() {
        return driverRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DriverDTO findById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        return toDTO(driver);
    }

    public DriverDTO create(DriverDTO driverDTO) {
        if (driverRepository.existsByFirstNameAndLastName(driverDTO.getFirstName(), driverDTO.getLastName())) {
            throw new DuplicateResourceException(
                    "Driver already exists with name: " + driverDTO.getFirstName() + " " + driverDTO.getLastName());
        }

        Driver driver = toEntity(driverDTO);

        if (driverDTO.getTeamId() != null) {
            Team team = teamRepository.findById(driverDTO.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team", driverDTO.getTeamId()));
            driver.setTeam(team);
        }

        Driver savedDriver = driverRepository.save(driver);
        return toDTO(savedDriver);
    }

    public DriverDTO update(Long id, DriverDTO driverDTO) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));

        driver.setFirstName(driverDTO.getFirstName());
        driver.setLastName(driverDTO.getLastName());
        driver.setNationality(driverDTO.getNationality());
        driver.setBirthDate(driverDTO.getBirthDate());

        if (driverDTO.getTeamId() != null) {
            Team team = teamRepository.findById(driverDTO.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team", driverDTO.getTeamId()));
            driver.setTeam(team);
        } else {
            driver.setTeam(null);
        }

        Driver updatedDriver = driverRepository.save(driver);
        return toDTO(updatedDriver);
    }

    @Transactional
    public void delete(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        driverRepository.delete(driver);
    }

    private DriverDTO toDTO(Driver driver) {
        return DriverDTO.builder()
                .id(driver.getId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .nationality(driver.getNationality())
                .birthDate(driver.getBirthDate())
                .teamId(driver.getTeam() != null ? driver.getTeam().getId() : null)
                .teamName(driver.getTeam() != null ? driver.getTeam().getName() : null)
                .build();
    }

    private Driver toEntity(DriverDTO dto) {
        return Driver.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nationality(dto.getNationality())
                .birthDate(dto.getBirthDate())
                .build();
    }
}