package com.zeroq.back.database.sensor;

import com.zeroq.back.common.datasource.RoutingDataSource;
import com.zeroq.back.database.sensor.entity.SensorCommand;
import com.zeroq.back.database.sensor.entity.SensorHeartbeat;
import com.zeroq.back.database.sensor.entity.SensorTelemetry;
import com.zeroq.back.database.sensor.entity.GatewayStatusSnapshot;
import com.zaxxer.hikari.HikariDataSource;
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
        basePackages = "com.zeroq.back.database.sensor.repository",
        entityManagerFactoryRef = "sensorEntityManagerFactory",
        transactionManagerRef = "sensorTransactionManager"
)
public class SensorDataConfig {

    @Value("${database.jpa.hibernate.ddl-auto:validate}")
    private String ddlAuto;

    @Value("${database.jpa.hibernate.dialect:org.hibernate.dialect.MySQLDialect}")
    private String hibernateDialect;

    @Bean
    @ConfigurationProperties("database.datasource.sensor.master")
    public DataSourceProperties sensorMasterDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.sensor.master.configure")
    public DataSource sensorMasterDatasource() {
        return sensorMasterDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("database.datasource.sensor.slave1")
    public DataSourceProperties sensorSlave1DatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.sensor.slave1.configure")
    public DataSource sensorSlave1Datasource() {
        return sensorSlave1DatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "sensorEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sensorEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sensorMasterDatasource") DataSource masterDataSource,
            @Qualifier("sensorSlave1Datasource") DataSource slave1DataSource
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
                                SensorTelemetry.class.getName(),
                                SensorHeartbeat.class.getName(),
                                SensorCommand.class.getName(),
                                GatewayStatusSnapshot.class.getName()
                        )
                )
                .properties(properties)
                .persistenceUnit("sensorEntityManager")
                .build();
    }

    @Bean(name = "sensorTransactionManager")
    public PlatformTransactionManager sensorTransactionManager(
            @Qualifier("sensorEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
    ) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
    }
}
