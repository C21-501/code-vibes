package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcExecutor;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.model.enums.ConfirmationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с исполнителями RFC
 */
@Repository
public interface RfcExecutorRepository extends JpaRepository<RfcExecutor, UUID> {

    /**
     * Найти исполнителей по RFC
     */
    List<RfcExecutor> findByRfc(Rfc rfc);

    /**
     * Найти исполнителей по ID RFC
     */
    List<RfcExecutor> findByRfcId(UUID rfcId);

    /**
     * Найти исполнителей по команде
     */
    List<RfcExecutor> findByTeam(Team team);

    /**
     * Найти исполнителей по ID команды
     */
    List<RfcExecutor> findByTeamId(UUID teamId);

    /**
     * Найти исполнителей по статусу подтверждения
     */
    List<RfcExecutor> findByConfirmationStatus(ConfirmationStatus confirmationStatus);

    /**
     * Найти конкретного исполнителя по RFC и команде
     */
    Optional<RfcExecutor> findByRfcAndTeam(Rfc rfc, Team team);

    /**
     * Найти конкретного исполнителя по ID RFC и ID команды
     */
    Optional<RfcExecutor> findByRfcIdAndTeamId(UUID rfcId, UUID teamId);

    /**
     * Проверить существование исполнителя для RFC и команды
     */
    boolean existsByRfcAndTeam(Rfc rfc, Team team);

    /**
     * Проверить существование исполнителя по ID RFC и ID команды
     */
    boolean existsByRfcIdAndTeamId(UUID rfcId, UUID teamId);
}
