FROM gradle:jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || return 0

COPY . .
RUN ./gradlew clean build -x test --no-daemon

FROM amazoncorretto:21
WORKDIR /app

COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

ENV SERVER_PORT=80
ENV TZ=Asia/Seoul

EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]
