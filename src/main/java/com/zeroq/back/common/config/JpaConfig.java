package com.zeroq.back.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
public class JpaConfig {
//    @Bean
//    public DataSource dataSource() {
//        return DataSourceBuilder.create().build();
//    }
//    @Bean
//    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
//        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
//    }
}
