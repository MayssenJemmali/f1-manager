package com.hamma.f1manager.service;

import com.hamma.f1manager.dto.TeamDTO;
import com.hamma.f1manager.exception.DuplicateResourceException;
import com.hamma.f1manager.exception.ResourceNotFoundException;
import com.hamma.f1manager.model.Team;
import com.hamma.f1manager.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    public List<TeamDTO> findAll() {
        return teamRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TeamDTO findById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        return toDTO(team);
    }

    public TeamDTO create(TeamDTO teamDTO) {
        if (teamRepository.existsByName(teamDTO.getName())) {
            throw new DuplicateResourceException("Team already exists with name: " + teamDTO.getName());
        }

        Team team = toEntity(teamDTO);
        Team savedTeam = teamRepository.save(team);
        return toDTO(savedTeam);
    }

    public TeamDTO update(Long id, TeamDTO teamDTO) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));

        // If name changed, check for duplicates
        if (!team.getName().equals(teamDTO.getName()) && teamRepository.existsByName(teamDTO.getName())) {
            throw new DuplicateResourceException("Team already exists with name: " + teamDTO.getName());
        }

        team.setName(teamDTO.getName());
        team.setCountry(teamDTO.getCountry());
        team.setFoundedDate(teamDTO.getFoundedDate());

        Team updatedTeam = teamRepository.save(team);
        return toDTO(updatedTeam);
    }

    @Transactional
    public void delete(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
        teamRepository.delete(team);
    }

    private TeamDTO toDTO(Team team) {
        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .country(team.getCountry())
                .foundedDate(team.getFoundedDate())
                .drivers(team.getDrivers().stream()
                        .map(driver -> com.hamma.f1manager.dto.DriverDTO.builder()
                                .id(driver.getId())
                                .firstName(driver.getFirstName())
                                .lastName(driver.getLastName())
                                .nationality(driver.getNationality())
                                .birthDate(driver.getBirthDate())
                                .teamId(driver.getTeam().getId())
                                .teamName(driver.getTeam().getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private Team toEntity(TeamDTO dto) {
        return Team.builder()
                .name(dto.getName())
                .country(dto.getCountry())
                .foundedDate(dto.getFoundedDate())
                .build();
    }
}