FROM mcr.microsoft.com/playwright/java:v1.42.0-jammy

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn dependency:resolve -q

CMD ["mvn", "test", "-Dheadless=true", "-Dbrowser=chromium"]
