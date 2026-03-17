# ==========================================
# STAGE 1: Build the application
# ==========================================
# We use the full JDK here so we have the tools to compile Java code
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy the Maven wrapper scripts and configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Give execution permission to the Maven wrapper
RUN chmod +x mvnw

# Download dependencies (This caches the internet downloads to make future builds faster)
RUN ./mvnw dependency:go-offline -B

# Copy your actual Java source code into the container
COPY src src

# Compile the code and create the .jar file (We skip tests because Jenkins already ran them!)
RUN ./mvnw clean package -DskipTests

# ==========================================
# STAGE 2: Run the application
# ==========================================
# We switch to the tiny JRE (Java Runtime Environment) to keep the final image small
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Magically copy ONLY the compiled .jar file from "STAGE 1" above
COPY --from=builder /build/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]