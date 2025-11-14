# -------------- Stage 0  ------------
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


EXPOSE 8080
# Set quyền file config
RUN mkdir -p /etc/mysql/conf.d
COPY mysql-conf/charset.cnf /etc/mysql/conf.d/charset.cnf
RUN chmod 644 /etc/mysql/conf.d/charset.cnf

# End