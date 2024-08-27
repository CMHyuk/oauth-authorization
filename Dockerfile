FROM amazoncorretto:17-alpine-jdk
WORKDIR /app

RUN apk update && apk upgrade --no-cache && apk add curl tar

COPY /target/ojt-minhyeok-authorization-*.jar /app/ojt-minhyeok-authorization.jar
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]
