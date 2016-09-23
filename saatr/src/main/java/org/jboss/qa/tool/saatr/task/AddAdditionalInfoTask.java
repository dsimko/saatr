
package org.jboss.qa.tool.saatr.task;

import java.net.URI;

import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(prefix="asking_for_additional_info", value="enable")
public class AddAdditionalInfoTask {

    private static final String BUILD_URL = "BUILD_URL";

    private static final String CONSOLE_TEXT = "consoleText";

    private final BuildRepository buildRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public AddAdditionalInfoTask(BuildRepository buildRepository, @Value("${asking_for_additional_info.jenkins.user}") String user,
            @Value("${asking_for_additional_info.jenkins.password}") String pass) {
        this.buildRepository = buildRepository;
        restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(user, pass));
    }

    @Scheduled(fixedRate = 1000 * 60 * 10) // every 10 minutes
    public void addAdditionalInfo() {
        log.info("Loading additional info started");
        for (BuildDocument buildDocument : buildRepository.findFailedWithoutAdditionalInfo()) {
            buildDocument.getVariables().stream().filter(p -> BUILD_URL.equals(p.getName())).findFirst().ifPresent(p -> {
                try {
                    String response = restTemplate.getForObject(new URI(p.getValue() + CONSOLE_TEXT), String.class);
                    buildRepository.addConsoleText(buildDocument, response);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }
}