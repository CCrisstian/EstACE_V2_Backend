# 1. Usamos Java 22 para que coincida con tu PC
FROM eclipse-temurin:22-jdk

# 2. Directorio de trabajo
WORKDIR /app

# 3. Copiamos TU jar específico (ignorando el .original)
COPY target/EstACE_V2-*.jar app.jar

# 4. Puerto
ENV PORT=8080
EXPOSE 8080

# 5. Arrancar
ENTRYPOINT ["java", "-jar", "app.jar"]