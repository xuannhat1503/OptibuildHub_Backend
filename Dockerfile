# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy toàn bộ mã nguồn
COPY . .

# Cấp quyền thực thi và loại bỏ CRLF cho mvnw, rồi build
RUN chmod +x mvnw \
    && sed -i 's/\r$//' mvnw \
    && ./mvnw -q -DskipTests package

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy JAR từ stage build
COPY --from=build /app/target/*.jar app.jar

# Render cấp biến PORT; app cần bind vào cổng này
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]