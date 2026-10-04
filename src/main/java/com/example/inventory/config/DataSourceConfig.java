package com.example.inventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        String rawUrl = env.getProperty("MYSQL_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = env.getProperty("DATABASE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = env.getProperty("SPRING_DATASOURCE_URL");
        }

        // If a full database URL is provided (Railway's MYSQL_URL or DATABASE_URL)
        if (rawUrl != null && !rawUrl.isBlank()) {
            try {
                if (rawUrl.startsWith("mysql://")) {
                    URI uri = new URI(rawUrl);
                    String host = uri.getHost();
                    int port = uri.getPort() != -1 ? uri.getPort() : 3306;
                    String path = uri.getPath();
                    String database = (path != null && path.length() > 1) ? path.substring(1) : "inventory_db";

                    String username = "root";
                    String password = "";
                    String userInfo = uri.getUserInfo();
                    if (userInfo != null) {
                        String[] parts = userInfo.split(":", 2);
                        username = parts[0];
                        if (parts.length > 1) {
                            password = parts[1];
                        }
                    }

                    String jdbcUrl = String.format(
                            "jdbc:mysql://%s:%d/%s?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                            host, port, database
                    );

                    log.info("Configured DataSource from MYSQL_URL/DATABASE_URL: host={}, port={}, db={}", host, port, database);

                    return DataSourceBuilder.create()
                            .driverClassName("com.mysql.cj.jdbc.Driver")
                            .url(jdbcUrl)
                            .username(username)
                            .password(password)
                            .build();
                } else if (rawUrl.startsWith("jdbc:mysql://")) {
                    String user = env.getProperty("MYSQLUSER", env.getProperty("MYSQL_USER", "root"));
                    String pass = env.getProperty("MYSQLPASSWORD", env.getProperty("MYSQL_PASSWORD", "tiger"));

                    return DataSourceBuilder.create()
                            .driverClassName("com.mysql.cj.jdbc.Driver")
                            .url(rawUrl)
                            .username(user)
                            .password(pass)
                            .build();
                }
            } catch (Exception e) {
                log.warn("Failed to parse URL '{}', falling back to individual host/port properties: {}", rawUrl, e.getMessage());
            }
        }

        // Fallback to separate Railway / environment variables
        String host = env.getProperty("MYSQLHOST", env.getProperty("MYSQL_HOST", "localhost"));
        String port = env.getProperty("MYSQLPORT", env.getProperty("MYSQL_PORT", "3306"));
        String database = env.getProperty("MYSQLDATABASE", env.getProperty("MYSQL_DATABASE", "inventory_db"));
        String user = env.getProperty("MYSQLUSER", env.getProperty("MYSQL_USER", "root"));
        String pass = env.getProperty("MYSQLPASSWORD", env.getProperty("MYSQL_PASSWORD", "tiger"));

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host, port, database
        );

        log.info("Configured DataSource from individual properties: host={}, port={}, db={}", host, port, database);

        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url(jdbcUrl)
                .username(user)
                .password(pass)
                .build();
    }
}
