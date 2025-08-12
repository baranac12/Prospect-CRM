package com.prospect.crm.scheduler;

import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.repository.UserSubsInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final UserSubsInfoRepository userSubsInfoRepository;

    // Her gece 00:00'da çalışır
    @Scheduled(cron = "0 0 0 * * ?")
    public void deactivateExpiredSubscriptions() {
        List<UserSubsInfo> expiredSubs = userSubsInfoRepository
                .findAllBySubsEndDateBeforeAndActiveTrue(LocalDateTime.now());

        expiredSubs.forEach(sub -> sub.setActive(false));
        userSubsInfoRepository.saveAll(expiredSubs);
    }
}
