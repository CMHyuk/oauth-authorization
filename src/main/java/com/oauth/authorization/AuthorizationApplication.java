package com.oauth.authorization;

import com.oauth.authorization.elasticsearch.base.CustomAwareRepositoryImpl;
import com.oauth.authorization.elasticsearch.config.CustomAwareRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@EnableElasticsearchRepositories(repositoryBaseClass = CustomAwareRepositoryImpl.class, repositoryFactoryBeanClass = CustomAwareRepositoryFactoryBean.class)
public class AuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationApplication.class, args);
    }

}
