package com.zeroq.back.database.admin;

import com.zeroq.back.common.datasource.RoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import com.zeroq.back.database.admin.entity.AdminAmenity;
import com.zeroq.back.database.admin.entity.AdminGateway;
import com.zeroq.back.database.admin.entity.AdminLocation;
import com.zeroq.back.database.admin.entity.AdminOccupancyData;
import com.zeroq.back.database.admin.entity.AdminOccupancyHistory;
import com.zeroq.back.database.admin.entity.AdminProfile;
import com.zeroq.back.database.admin.entity.AdminSensor;
import com.zeroq.back.database.admin.entity.AdminSpace;
import com.zeroq.back.database.admin.entity.Notification;
import com.zeroq.back.database.admin.entity.NotificationPreference;
import com.zeroq.back.database.admin.entity.UserPreference;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = {"com.zeroq.back.database.admin.repository"},
        entityManagerFactoryRef = "adminEntityManagerFactory",
        transactionManagerRef = "adminTransactionManager"
)
public class AdminDataConfig {

    @Value("${database.jpa.hibernate.ddl-auto:validate}")
    private String ddlAuto;

    @Value("${database.jpa.hibernate.dialect:org.hibernate.dialect.MySQLDialect}")
    private String hibernateDialect;

    @Bean
    @ConfigurationProperties("database.datasource.admin.master")
    public DataSourceProperties adminMasterDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.admin.master.configure")
    public DataSource adminMasterDatasource() {
        return adminMasterDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("database.datasource.admin.slave1")
    public DataSourceProperties adminSlave1DatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.admin.slave1.configure")
    public DataSource adminSlave1Datasource() {
        return adminSlave1DatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "adminEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean adminEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("adminMasterDatasource") DataSource masterDataSource,
            @Qualifier("adminSlave1Datasource") DataSource slave1DataSource
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> datasourceMap = new HashMap<>() {{
            put("master", masterDataSource);
            put("slave", slave1DataSource);
        }};

        routingDataSource.setTargetDataSources(datasourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.afterPropertiesSet();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", ddlAuto);
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.default_batch_fetch_size", 200);
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.use_sql_comments", true);

        return builder.dataSource(new LazyConnectionDataSourceProxy(routingDataSource))
                .managedTypes(
                        PersistenceManagedTypes.of(
                                AdminSpace.class.getName(),
                                AdminLocation.class.getName(),
                                AdminAmenity.class.getName(),
                                AdminOccupancyData.class.getName(),
                                AdminOccupancyHistory.class.getName(),
                                AdminSensor.class.getName(),
                                AdminGateway.class.getName(),
                                AdminProfile.class.getName(),
                                UserPreference.class.getName(),
                                NotificationPreference.class.getName(),
                                Notification.class.getName()
                        )
                )
                .properties(properties)
                .persistenceUnit("adminEntityManager")
                .build();
    }

    @Bean(name = "adminTransactionManager")
    public PlatformTransactionManager adminTransactionManager(
            @Qualifier("adminEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
    ) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
    }
}
