FROM amazoncorretto:17-alpine-jdk as MAVEN_BUILD
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

COPY pom.xml .
RUN mvn -e -B dependency:resolve dependency:resolve-plugins

COPY src ./src
RUN mvn -e -B package -Dmaven.test.skip=true

FROM amazoncorretto:16 as DOCKER_BUILD
WORKDIR /app
COPY --from=MAVEN_BUILD /app/target/authorization-*.jar /app/authorization.jar
ENTRYPOINT ["java", "-jar", "authorization.jar"]
