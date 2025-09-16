package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcReviewer;
import ru.c21501.rfcservice.model.entity.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с согласующими RFC
 */
@Repository
public interface RfcReviewerRepository extends JpaRepository<RfcReviewer, UUID> {
    
    /**
     * Найти согласующих по RFC
     */
    List<RfcReviewer> findByRfc(Rfc rfc);
    
    /**
     * Найти согласующих по ID RFC
     */
    List<RfcReviewer> findByRfcId(String rfcId);
    
    /**
     * Найти согласующих по команде
     */
    List<RfcReviewer> findByTeam(Team team);
    
    /**
     * Найти согласующих по ID команды
     */
    List<RfcReviewer> findByTeamId(UUID teamId);
    
    /**
     * Найти конкретного согласующего по RFC и команде
     */
    Optional<RfcReviewer> findByRfcAndTeam(Rfc rfc, Team team);
    
    /**
     * Найти конкретного согласующего по ID RFC и ID команды
     */
    Optional<RfcReviewer> findByRfcIdAndTeamId(String rfcId, UUID teamId);
    
    /**
     * Проверить существование согласующего для RFC и команды
     */
    boolean existsByRfcAndTeam(Rfc rfc, Team team);
    
    /**
     * Проверить существование согласующего по ID RFC и ID команды
     */
    boolean existsByRfcIdAndTeamId(String rfcId, UUID teamId);
}
