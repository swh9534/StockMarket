# 🔹 빌드 스테이지
FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache bash curl

WORKDIR /app

# Maven Wrapper 및 의존성 다운로드
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# 프로젝트 코드 복사 후 빌드
COPY . .

RUN ./mvnw package -DskipTests

# 🔹 런타임 스테이지
FROM eclipse-temurin:17-jre-alpine as runtime

WORKDIR /app

# 시스템 사용자 추가
RUN addgroup -g 1000 worker && \
    adduser -u 1000 -G worker -s /bin/sh -D worker

COPY --from=build /app/target/*.jar ./main.jar

RUN chown worker:worker ./main.jar

USER worker:worker

# 기본 프로파일 지정 (없을 경우 'dev'로 실행)
ENV PROFILE=${PROFILE:-dev}

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "main.jar"]