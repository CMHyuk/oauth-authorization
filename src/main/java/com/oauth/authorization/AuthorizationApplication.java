package com.oauth.authorization;

import com.oauth.authorization.elasticsearch.base.CustomAwareRepositoryImpl;
import com.oauth.authorization.elasticsearch.config.CustomAwareRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

// spring-data-jpa 기본 설정의 dataSource 설정을 하지 않도록 추가
// BasicErrorController 를 설정하지 않도록 추가 ( /error 페이지로 넘기는 것을 방지 )
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
// ElasticSearchJpaRepository 대신 커스텀 클래스를 지정
@EnableElasticsearchRepositories(repositoryBaseClass = CustomAwareRepositoryImpl.class, repositoryFactoryBeanClass = CustomAwareRepositoryFactoryBean.class)
public class AuthorizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationApplication.class, args);
    }

}
