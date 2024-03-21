package edu.java.scheduler;

import edu.java.configuration.ApplicationConfig;
import edu.java.model.Link;
import edu.java.model.LinkUpdateInfo;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdaterService;
import edu.java.service.SendUpdatesService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableScheduling
@Configuration
@ConditionalOnExpression("${app.scheduler.enable}")
public class LinkUpdaterScheduler {

    private final LinkService linkService;
    private final ApplicationConfig.LinkChecker linkCheckerConfig;
    private final LinkUpdaterService updaterService;
    private final SendUpdatesService sendUpdatesService;

    @Autowired
    public LinkUpdaterScheduler(
        LinkService linkService,
        ApplicationConfig applicationConfig,
        LinkUpdaterService updaterService,
        SendUpdatesService sendUpdatesService
    ) {
        this.linkService = linkService;
        this.linkCheckerConfig = applicationConfig.linkChecker();
        this.updaterService = updaterService;
        this.sendUpdatesService = sendUpdatesService;
    }

    @Scheduled(fixedDelayString = "#{['app-edu.java.configuration.ApplicationConfig'].scheduler().interval()}")
    public void update() {
        OffsetDateTime lastLinkCheckingTime = OffsetDateTime.now().minus(linkCheckerConfig.checkInterval());

        List<Link> needCheckingLinks = linkService.findAllWhereLastCheckTimeBefore(lastLinkCheckingTime);

        needCheckingLinks.stream()
            .map(updaterService::checkUpdates)
            .filter(LinkUpdateInfo::isUpdated)
            .peek(updateInfo -> linkService.setLastUpdateTime(
                linkService.findByUrl(URI.create(updateInfo.getUrl())).getId(),
                OffsetDateTime.now()
            ))
            .forEach(sendUpdatesService::sendUpdate);
    }

}
