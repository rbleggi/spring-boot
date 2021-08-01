package br.com.croa.api.modules.croa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "br.com.croa.api.modules.croa.repository")
@EntityScan(basePackages = "br.com.croa.api.modules.croa.entity")
public class CroaModule {

}
