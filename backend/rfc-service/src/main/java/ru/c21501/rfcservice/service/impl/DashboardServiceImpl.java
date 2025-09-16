package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.dto.response.DashboardResponse;
import ru.c21501.rfcservice.dto.response.RecentRfcResponse;
import ru.c21501.rfcservice.dto.response.RfcStatsResponse;
import ru.c21501.rfcservice.dto.response.UpcomingDeadlineResponse;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.service.DashboardService;

import java.util.List;

/**
 * Реализация сервиса для работы с данными дашборда
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    
    private final RfcRepository rfcRepository;
    
    @Override
    public DashboardResponse getDashboardData() {
        log.info("Getting dashboard data");
        
        DashboardResponse response = new DashboardResponse();
        
        // Статистика
        RfcStatsResponse stats = new RfcStatsResponse();
        stats.setTotal(rfcRepository.count());
        stats.setDraft(rfcRepository.countByStatus(RfcStatus.REQUESTED_NEW));
        stats.setReview(rfcRepository.countByStatus(RfcStatus.WAITING));
        stats.setApproved(rfcRepository.countByStatus(RfcStatus.APPROVED));
        stats.setImplemented(rfcRepository.countByStatus(RfcStatus.DONE));
        stats.setRejected(rfcRepository.countByStatus(RfcStatus.DECLINED) + 
                         rfcRepository.countByStatus(RfcStatus.CANCELLED));
        
        response.setStats(stats);
        
        // Последние RFC
        List<Rfc> recentRfcs = rfcRepository.findTop10ByOrderByCreatedDateDesc();
        List<RecentRfcResponse> recentRfcResponses = recentRfcs.stream()
                .map(this::mapToRecentRfcResponse)
                .toList();
        response.setRecentRfcs(recentRfcResponses);
        
        // Ближайшие дедлайны
        List<Rfc> upcomingDeadlines = rfcRepository.findUpcomingDeadlines();
        List<UpcomingDeadlineResponse> upcomingDeadlineResponses = upcomingDeadlines.stream()
                .limit(10)
                .map(this::mapToUpcomingDeadlineResponse)
                .toList();
        response.setUpcomingDeadlines(upcomingDeadlineResponses);
        
        log.info("Dashboard data retrieved: {} total RFCs, {} recent RFCs, {} upcoming deadlines", 
                stats.getTotal(), recentRfcResponses.size(), upcomingDeadlineResponses.size());
        
        return response;
    }
    
    private RecentRfcResponse mapToRecentRfcResponse(Rfc rfc) {
        RecentRfcResponse response = new RecentRfcResponse();
        response.setId(rfc.getId());
        response.setTitle(rfc.getTitle());
        response.setStatus(rfc.getStatus());
        response.setPriority(rfc.getPriority());
        response.setCreatedDate(rfc.getCreatedDate());
        response.setAuthorName(rfc.getInitiator() != null ? 
                rfc.getInitiator().getFirstName() + " " + rfc.getInitiator().getLastName() : 
                "Неизвестно");
        return response;
    }
    
    private UpcomingDeadlineResponse mapToUpcomingDeadlineResponse(Rfc rfc) {
        UpcomingDeadlineResponse response = new UpcomingDeadlineResponse();
        response.setId(rfc.getId());
        response.setTitle(rfc.getTitle());
        response.setDeadline(rfc.getPlannedDate());
        response.setStatus(rfc.getStatus());
        return response;
    }
}
