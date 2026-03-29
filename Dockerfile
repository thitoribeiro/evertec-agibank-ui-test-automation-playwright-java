FROM mcr.microsoft.com/playwright/java:v1.42.0-jammy

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Pre-download all Maven dependencies and plugins at build time
# This avoids re-downloading on every `docker compose up`
RUN mvn dependency:resolve -q && \
    mvn dependency:resolve-plugins -q

# Default environment variables (can be overridden at runtime)
ENV HEADLESS=true \
    BROWSER=chromium \
    TIMEOUT=30000 \
    BASE_URL=https://blog.agibank.com.br

# Entrypoint: run tests, then generate the Allure HTML report.
# Using ';' (not '&&') so the report is generated even when tests fail.
CMD ["sh", "-c", \
  "mvn test \
     -Dheadless=${HEADLESS} \
     -Dbrowser=${BROWSER} \
     -Dtimeout=${TIMEOUT} \
     -Dbase.url=${BASE_URL}; \
   mvn allure:report -q && \
   echo '' && \
   echo '============================================================' && \
   echo '  Tests finished. Report generated at target/allure-report  ' && \
   echo '============================================================'"]
