package gms4u.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableJpaRepositories("gms4u.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @LiquibaseDataSource
    @ConfigurationProperties("spring.liquibase")
    public DataSource liquibaseDataSource() {
        DataSource build = DataSourceBuilder.create().username(dataSourceProperties().getUsername()).password(dataSourceProperties().getPassword()).url(dataSourceProperties().getUrl()).build();
        if (build instanceof HikariDataSource) {
            ((HikariDataSource) build).setMaximumPoolSize(3);
            ((HikariDataSource) build).setPoolName("Liquibase Pool");
        }
        return build;
    }

//    @PersistenceContext(unitName = "primary")
//    @Primary
//    @Bean(name = "entityManager")
//    public LocalContainerEntityManagerFactoryBean mySqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
//        DataSource dataSource = dataSource();
//        return builder.dataSource(dataSource).persistenceUnit("primary")
//            .packages("gms4u.domain").build();
//    }
//
//    @Primary
//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
//        JpaTransactionManager tm = new JpaTransactionManager();
//        tm.setEntityManagerFactory(mySqlEntityManagerFactory(builder).getObject());
//        tm.setDataSource(dataSource());
//        return tm;
//    }
//
//    @Bean("liquibaseDataSource")
//    @LiquibaseDataSource
////    @ConfigurationProperties("spring.datasource")
//    public DataSource liquibaseDataSource() {
//        return DataSourceBuilder.create().build();
//    }

    @Bean
    @QuartzDataSource
    @ConfigurationProperties("spring.quartz.datasource")
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create().build();
    }

}
