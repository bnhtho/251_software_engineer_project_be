# ========= STAGE 1: BUILD với Maven Wrapper (mvnw) =========
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Bước 1: Copy file cần thiết cho Maven
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Bước 2: Tải dependencies để tối ưu cache
RUN chmod +x ./mvnw && \
    ./mvnw dependency:go-offline

# Bước 3: Copy source code
COPY src ./src

# Bước 4: Build
RUN ./mvnw clean package -DskipTests

# ========= STAGE 2: RUNTIME =========
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

RUN mkdir -p /etc/mysql/conf.d
COPY mysql-conf/charset.cnf /etc/mysql/conf.d/charset.cnf
RUN chmod 644 /etc/mysql/conf.d/charset.cnf

CMD ["java", "-jar", "app.jar"]
