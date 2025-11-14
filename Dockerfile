
# Set environment for utf-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# ========= STAGE 1: BUILD với Maven Wrapper (mvnw) + Java 21 =========
FROM eclipse-temurin AS build
WORKDIR /app

# Copy Maven Wrapper (mvnw, mvnw.cmd, .mvn) – bắt buộc để dùng wrapper
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn

# Copy pom.xml và source
COPY pom.xml .
COPY src ./src

# Cấp quyền + Build bằng Maven Wrapper (không cần cài Maven toàn cục)
RUN chmod +x ./mvnw && \
    ./mvnw clean package -DskipTests -Dmaven.repo.local=.m2

# ========= STAGE 2: Runtime với JRE 21 (nhẹ hơn JDK) =========
FROM eclipse-temurin
WORKDIR /app

# Copy JAR từ stage build
COPY --from=build /app/target/*.jar app.jar

# Copy entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080

# Chạy entrypoint → chờ DB → chạy app
ENTRYPOINT ["/entrypoint.sh"]
