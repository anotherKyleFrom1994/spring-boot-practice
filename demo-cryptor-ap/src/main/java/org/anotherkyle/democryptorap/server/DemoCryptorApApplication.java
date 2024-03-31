package org.anotherkyle.democryptorap.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.anotherkyle.commonlib.ApplicationStatus;
import org.anotherkyle.commonlib.util.JsonUtil;
import org.anotherkyle.democryptorap.common.ApplicationConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {ApplicationConstants.ROOT_PACKAGE})
//@EnableDiscoveryClient
//@EnableEurekaClient
@EnableScheduling
public class DemoCryptorApApplication {

    public static void main(String[] args) {
        ApplicationStatus.setApplicationName(ApplicationConstants.APPLICATION_CODE);
        SpringApplication.run(DemoCryptorApApplication.class, args);
    }

    @Bean
    ObjectMapper getObjectMapper() {
        return JsonUtil.objectMapper;
    }

}
