# 1. Imagen Base, exactamente la misma versión de Java que tiengo instalada
FROM amazoncorretto:22

# 2. Directorio de trabajo
WORKDIR /app

# 3. Copiamos TU jar específico (ignorando el .original)
COPY target/EstACE_V2-*.jar app.jar

# 4. Puerto
ENV PORT=8080
EXPOSE 8080

# 5. Arrancar
ENTRYPOINT ["java", "-jar", "app.jar"]