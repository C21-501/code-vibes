package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.exception.ResourceNotFoundException;
import ru.c21501.rfcservice.exception.SubsystemAlreadyExistsException;
import ru.c21501.rfcservice.model.entity.SubsystemEntity;
import ru.c21501.rfcservice.model.entity.SystemEntity;
import ru.c21501.rfcservice.model.entity.TeamEntity;
import ru.c21501.rfcservice.repository.SubsystemRepository;
import ru.c21501.rfcservice.repository.SystemRepository;
import ru.c21501.rfcservice.repository.TeamRepository;
import ru.c21501.rfcservice.service.SubsystemService;

import java.util.List;

/**
 * Реализация сервиса для работы с подсистемами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubsystemServiceImpl implements SubsystemService {

    private final SubsystemRepository subsystemRepository;
    private final SystemRepository systemRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public SubsystemEntity createSubsystem(SubsystemEntity subsystemEntity, Long systemId, Long teamId) {
        log.info("Creating subsystem: {} for system ID: {}", subsystemEntity.getName(), systemId);

        // Проверяем существование системы
        SystemEntity system = systemRepository.findById(systemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("System with ID %d not found", systemId)
                ));

        // Проверяем существование команды
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Team with ID %d not found", teamId)
                ));

        // Проверяем, что подсистема с таким названием еще не существует в данной системе
        if (subsystemRepository.existsByNameAndSystemId(subsystemEntity.getName(), systemId)) {
            throw new SubsystemAlreadyExistsException(
                    String.format("Subsystem with name '%s' already exists in system %d",
                            subsystemEntity.getName(), systemId)
            );
        }

        // Устанавливаем связи
        subsystemEntity.setSystem(system);
        subsystemEntity.setTeam(team);

        // Сохраняем подсистему в БД
        SubsystemEntity savedSubsystem = subsystemRepository.save(subsystemEntity);
        log.info("Subsystem saved to database with ID: {}", savedSubsystem.getId());

        return savedSubsystem;
    }

    @Override
    @Transactional
    public SubsystemEntity updateSubsystem(Long id, SubsystemEntity subsystemEntity, Long systemId, Long teamId) {
        log.info("Updating subsystem with ID: {}", id);

        // Проверяем, что подсистема существует
        SubsystemEntity existingSubsystem = getSubsystemById(id);

        // Проверяем существование системы
        SystemEntity system = systemRepository.findById(systemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("System with ID %d not found", systemId)
                ));

        // Проверяем существование команды
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Team with ID %d not found", teamId)
                ));

        // Проверяем, что новое название не занято другой подсистемой в этой системе
        if (!existingSubsystem.getName().equals(subsystemEntity.getName())
                && subsystemRepository.existsByNameAndSystemId(subsystemEntity.getName(), systemId)) {
            throw new SubsystemAlreadyExistsException(
                    String.format("Subsystem with name '%s' already exists in system %d",
                            subsystemEntity.getName(), systemId)
            );
        }

        // Обновляем данные
        existingSubsystem.setName(subsystemEntity.getName());
        existingSubsystem.setDescription(subsystemEntity.getDescription());
        existingSubsystem.setSystem(system);
        existingSubsystem.setTeam(team);

        // Сохраняем обновленную подсистему
        SubsystemEntity updatedSubsystem = subsystemRepository.save(existingSubsystem);
        log.info("Subsystem updated successfully: {}", updatedSubsystem.getId());

        return updatedSubsystem;
    }

    @Override
    @Transactional(readOnly = true)
    public SubsystemEntity getSubsystemById(Long id) {
        log.info("Getting subsystem by ID: {}", id);
        return subsystemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Subsystem with ID %d not found", id)
                ));
    }

    @Override
    @Transactional
    public void deleteSubsystem(Long id) {
        log.info("Deleting subsystem with ID: {}", id);

        // Проверяем, что подсистема существует
        SubsystemEntity subsystem = getSubsystemById(id);

        // Удаляем подсистему из БД
        subsystemRepository.delete(subsystem);
        log.info("Subsystem deleted from database: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubsystemEntity> getSubsystemsBySystemId(Long systemId) {
        log.info("Getting subsystems for system ID: {}", systemId);

        // Проверяем, что система существует
        if (!systemRepository.existsById(systemId)) {
            throw new ResourceNotFoundException(
                    String.format("System with ID %d not found", systemId)
            );
        }

        List<SubsystemEntity> subsystems = subsystemRepository.findBySystemId(systemId);
        log.info("Found {} subsystems for system {}", subsystems.size(), systemId);

        return subsystems;
    }
}