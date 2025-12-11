FROM gradle:jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM amazoncorretto:21
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV SERVER_PORT=80
ENV TZ=Asia/Seoul

EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]
