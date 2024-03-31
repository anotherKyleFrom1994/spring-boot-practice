package org.anotherkyle.democryptorap.server;

import org.anotherkyle.democryptorap.common.ApplicationConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = {ApplicationConstants.ROOT_PACKAGE})
@EnableR2dbcAuditing
public class R2dbcRepositoryConfig {
}
