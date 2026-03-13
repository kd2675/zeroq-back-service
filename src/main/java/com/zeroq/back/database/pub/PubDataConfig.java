package com.zeroq.back.database.pub;

import com.zaxxer.hikari.HikariDataSource;
import com.zeroq.back.common.datasource.RoutingDataSource;
import com.zeroq.back.database.pub.entity.Favorite;
import com.zeroq.back.database.pub.entity.ProfileUser;
import com.zeroq.back.database.pub.entity.Review;
import com.zeroq.back.database.pub.entity.UserBehavior;
import com.zeroq.back.database.pub.entity.UserLocation;
import com.zeroq.back.database.pub.repository.FavoriteRepository;
import com.zeroq.back.database.pub.repository.ProfileUserRepository;
import com.zeroq.back.database.pub.repository.ReviewRepository;
import com.zeroq.back.database.pub.repository.UserBehaviorRepository;
import com.zeroq.back.database.pub.repository.UserLocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackageClasses = {
                ReviewRepository.class,
                FavoriteRepository.class,
                UserLocationRepository.class,
                ProfileUserRepository.class,
                UserBehaviorRepository.class
        },
        includeFilters = {
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ReviewRepository.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FavoriteRepository.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = UserLocationRepository.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProfileUserRepository.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = UserBehaviorRepository.class)
        },
        entityManagerFactoryRef = "pubEntityManagerFactory",
        transactionManagerRef = "pubTransactionManager"
)
public class PubDataConfig {

    @Value("${database.jpa.hibernate.ddl-auto:validate}")
    private String ddlAuto;

    @Value("${database.jpa.hibernate.dialect:org.hibernate.dialect.MySQLDialect}")
    private String hibernateDialect;

    @Bean
    @Primary
    @ConfigurationProperties("database.datasource.service.master")
    public DataSourceProperties pubMasterDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("database.datasource.service.master.configure")
    public DataSource pubMasterDatasource() {
        return pubMasterDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("database.datasource.service.slave1")
    public DataSourceProperties pubSlave1DatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.service.slave1.configure")
    public DataSource pubSlave1Datasource() {
        return pubSlave1DatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "pubEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean pubEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("pubMasterDatasource") DataSource masterDataSource,
            @Qualifier("pubSlave1Datasource") DataSource slave1DataSource
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> datasourceMap = new HashMap<Object, Object>() {
            {
                put("master", masterDataSource);
                put("slave", slave1DataSource);
            }
        };

        routingDataSource.setTargetDataSources(datasourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.afterPropertiesSet();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", ddlAuto);
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.default_batch_fetch_size", 1000);
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.use_sql_comments", true);

        return builder.dataSource(new LazyConnectionDataSourceProxy(routingDataSource))
                .managedTypes(
                        PersistenceManagedTypes.of(
                                Review.class.getName(),
                                Favorite.class.getName(),
                                UserLocation.class.getName(),
                                ProfileUser.class.getName(),
                                UserBehavior.class.getName()
                        )
                )
                .properties(properties)
                .persistenceUnit("pubEntityManager")
                .build();
    }

    @Bean(name = "pubTransactionManager")
    @Primary
    public PlatformTransactionManager pubTransactionManager(
            final @Qualifier("pubEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
    ) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
    }
}
