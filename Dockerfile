FROM amazoncorretto:17-alpine-jdk as MAVEN_BUILD
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

COPY pom.xml .
RUN mvn -e -B dependency:resolve dependency:resolve-plugins

COPY src ./src
RUN mvn -e -B package -Dmaven.test.skip=true

FROM amazoncorretto:17-alpine-jdk as DOCKER_BUILDdoc

COPY --from=MAVEN_BUILD /app/target/*.jar /app/authorization-minhyeok.jar
CMD ["java", "-jar", "/app/authorization-minhyeok.jar"]