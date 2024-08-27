FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY target/*.jar /app/ojt-minhyeok-authorization.jar
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]