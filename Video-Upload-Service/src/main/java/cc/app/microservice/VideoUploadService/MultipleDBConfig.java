package cc.app.microservice.VideoUploadService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MultipleDBConfig {

    @Bean(name = "uploadInfoDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource userInfoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "uploadInfoJdbcTemplate")
    public JdbcTemplate userInfoJdbcTemplate(@Qualifier("uploadInfoDataSource") DataSource uiDataSource) {
        return new JdbcTemplate(uiDataSource);
    }

    @Bean(name = "userAuthDataSource")
    @ConfigurationProperties(prefix = "spring.second-db")
    public DataSource userAuthDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "userAuthJdbcTemplate")
    public JdbcTemplate userAuthJdbcTemplate(@Qualifier("userAuthDataSource") DataSource uaDataSource) {
        return new JdbcTemplate(uaDataSource);
    }

}
