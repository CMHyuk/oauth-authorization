FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY /target/*.jar /app/ojt-minhyeok-authorization.jar

CMD ["java", "-jar", "/app/ojt-minhyeok-authorization.jar"]