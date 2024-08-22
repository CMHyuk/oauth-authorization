FROM amazoncorretto:17-alpine-jdk as MAVEN_BUILD
WORKDIR /build

COPY pom.xml .
RUN mvn -e -B dependency:resolve dependency:resolve-plugins

COPY src ./src
RUN mvn -e -B package -Dmaven.test.skip=true

FROM amazoncorretto:16 as DOCKER_BUILD
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/jenkins-authorization-*.jar /app/jenkins-authorization.jar
ENTRYPOINT ["java", "-jar", "jenkins-authorization.jar"]