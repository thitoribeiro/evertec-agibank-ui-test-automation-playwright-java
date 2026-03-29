# Agi Blog — UI Test Automation

[![CI — UI Tests](https://github.com/thitoribeiro/evertec-agibank-ui-test-automation-playwright-java/actions/workflows/tests.yml/badge.svg)](https://github.com/thitoribeiro/evertec-agibank-ui-test-automation-playwright-java/actions/workflows/tests.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-blue)](https://thitoribeiro.github.io/evertec-agibank-ui-test-automation-playwright-java/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Playwright](https://img.shields.io/badge/Playwright-1.42.0-45ba4b?logo=playwright&logoColor=white)](https://playwright.dev/java/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5.10.2-25A162)](https://junit.org/junit5/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Automated end-to-end UI test suite for [blog.agibank.com.br](https://blog.agibank.com.br), a WordPress blog running the Astra Child theme. Built with **Java 17**, **Playwright**, **JUnit 5**, and **Allure Report**, with full CI/CD via **GitHub Actions** and containerised execution via **Docker**.

---

## Table of Contents

- [About the Project](#about-the-project)
- [Test Coverage](#test-coverage)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Running Tests Locally](#running-tests-locally)
- [Running with Docker](#running-with-docker)
- [Allure Report](#allure-report)
- [GitHub Actions CI/CD](#github-actions-cicd)
- [Branch Strategy](#branch-strategy)

---

## About the Project

This project validates the core user journeys on the Agi Blog. All tests follow the **Page Object Model (POM)** pattern:

- **Selectors** are externalised to `.properties` files under `src/test/resources/selectors/`, never hardcoded.
- **`ElementMap`** is a utility that loads selectors at runtime and supports multi-fallback resolution — if the first selector fails, the next is tried automatically.
- **`BaseTest`** manages the full Playwright browser lifecycle, sets Brazilian locale (`pt-BR`), and attaches a full-page screenshot to the Allure report after every test.

---

## Test Coverage

| Suite | Class | Scenarios | Feature |
|---|---|:---:|---|
| Homepage | `HomepageTest` | 4 | Logo, hero carousel, article listing |
| Search | `SearchTest` | 8 | Valid/invalid terms, special chars, parameterised categories |
| Article | `ArticleTest` | 5 | Metadata, breadcrumb, content, URL slug |
| Navigation | `NavigationTest` | 4 | Multi-level dropdown menu, category filter |
| Pagination | `PaginationTest` | 5 | Next/prev, direct page number, URL params |
| **Total** | | **26** | |

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 (LTS) | Language runtime |
| Maven | 3.9+ | Build & dependency management |
| Playwright Java | 1.42.0 | Browser automation |
| JUnit Jupiter | 5.10.2 | Test framework (API, Engine, Params) |
| Allure JUnit5 | 2.27.0 | Test reporting |
| AspectJ Weaver | 1.9.22 | Allure Java agent instrumentation |
| SLF4J Simple | 2.0.12 | Logging |
| Docker | 20.10+ | Containerised execution |
| GitHub Actions | — | CI/CD pipeline |

---

## Prerequisites

| Tool | Version | Installation |
|---|---|---|
| JDK | 17+ | [Temurin](https://adoptium.net/) or `brew install temurin@17` |
| Maven | 3.9+ | [maven.apache.org](https://maven.apache.org/) or `brew install maven` |
| Docker Desktop | 4.x+ | [docker.com](https://www.docker.com/products/docker-desktop/) |
| Docker Compose | v2 (CLI plugin) | Included with Docker Desktop |
| Allure CLI *(optional)* | 2.27.0 | `brew install allure` |

> The Playwright browser binaries are downloaded automatically on first run.
> No separate Node.js or browser installation is required.

---

## Project Structure

```
evertec-agibank-ui-test-automation-playwright-java/
├── .github/
│   └── workflows/
│       └── tests.yml               # CI/CD — GitHub Actions pipeline
├── src/
│   └── test/
│       ├── java/com/agi/blog/
│       │   ├── base/
│       │   │   └── BaseTest.java   # Playwright lifecycle + Allure screenshot
│       │   ├── pages/
│       │   │   ├── HomePage.java
│       │   │   ├── SearchPage.java
│       │   │   ├── ArticlePage.java
│       │   │   ├── NavigationPage.java
│       │   │   └── PaginationPage.java
│       │   ├── tests/
│       │   │   ├── HomepageTest.java
│       │   │   ├── SearchTest.java
│       │   │   ├── ArticleTest.java
│       │   │   ├── NavigationTest.java
│       │   │   └── PaginationTest.java
│       │   └── utils/
│       │       └── ElementMap.java # Loads selectors from .properties with fallback
│       └── resources/
│           └── selectors/
│               ├── homepage.properties
│               ├── search.properties
│               ├── article.properties
│               ├── navigation.properties
│               └── pagination.properties
├── Dockerfile                      # Playwright Java base image + deps pre-cached
├── docker-compose.yml              # test-runner + report-server (nginx)
├── pom.xml                         # Maven — dependencies and plugins
└── README.md
```

---

## Configuration

All runtime parameters are passed as Maven system properties and have sensible defaults.

| Property | Default | Description |
|---|---|---|
| `base.url` | `https://blog.agibank.com.br` | Target application URL |
| `headless` | `true` | Run browser headlessly |
| `browser` | `chromium` | Browser: `chromium`, `firefox`, `webkit` |
| `slow.mo` | `0` | Slow-motion delay in ms (useful for debugging) |
| `timeout` | `30000` | Default element timeout in ms |

Override at the command line:

```bash
mvn test -Dheadless=false -Dbrowser=firefox -Dslow.mo=200
```

### Selector Externalisation

Selectors live in `.properties` files — one file per page. Each key supports multiple CSS/Playwright fallbacks separated by `|`. At runtime, `ElementMap` tries them left-to-right and returns the first match.

```properties
# search.properties
input=input[name="s"]|input[placeholder*="Pesquisar" i]|input[type="search"]
```

---

## Running Tests Locally

### 1. Install Playwright Chromium (first time only)

```bash
mvn exec:java \
  -e -D exec.mainClass=com.microsoft.playwright.CLI \
  -D exec.args="install chromium --with-deps"
```

### 2. Run all tests

```bash
mvn test
```

### 3. Run a specific test class

```bash
mvn test -Dtest=SearchTest
```

### 4. Run in headed mode (browser visible)

```bash
mvn test -Dheadless=false -Dslow.mo=300
```

### 5. Run on a different browser

```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=webkit
```

### 6. Generate and open Allure Report

```bash
# Generate static HTML report at target/allure-report/
mvn allure:report

# Generate + serve on localhost (opens browser automatically)
mvn allure:serve
```

---

## Running with Docker

Docker runs the full test suite inside the official Playwright Java container and serves the Allure report via nginx — **no local Java or browser installation required**.

### Build the image

```bash
docker compose build
```

### Run tests + view report in the browser

```bash
docker compose up --abort-on-container-exit
```

1. Tests execute inside the `test-runner` container (headless Chromium).
2. The Allure HTML report is generated at `target/allure-report/`.
3. The `report-server` container (nginx) starts automatically and serves the report.
4. Open **[http://localhost:8080](http://localhost:8080)** in your browser.
5. Press `Ctrl+C` to stop the server when done.

### Run tests only (no report server)

```bash
docker compose run --rm test-runner
```

### Override run parameters

```bash
HEADLESS=false BROWSER=firefox docker compose up --abort-on-container-exit
```

### Available environment variables

| Variable | Default | Description |
|---|---|---|
| `HEADLESS` | `true` | `false` to run with a visible browser (requires display) |
| `BROWSER` | `chromium` | `chromium`, `firefox`, or `webkit` |
| `TIMEOUT` | `30000` | Element wait timeout in milliseconds |
| `BASE_URL` | `https://blog.agibank.com.br` | Override to point at a staging environment |

### Test artifacts on the host

After a Docker run, the following directories are mapped to your project folder:

| Host Path | Contents |
|---|---|
| `target/allure-results/` | Raw Allure result files (JSON + screenshots) |
| `target/allure-report/` | Generated HTML report |
| `target/surefire-reports/` | JUnit XML and TXT reports |

---

## Allure Report

### Report features

- **Epics / Features / Stories** — full hierarchy matching the test annotations
- **Severity levels** — BLOCKER → CRITICAL → NORMAL → MINOR
- **Screenshots** — full-page screenshot attached to every test (pass and fail)
- **History & Trend** — tracks pass rate across CI runs via GitHub Pages
- **Retries** — surfaces flaky tests across executions

### Allure annotations

```java
@Epic("Blog do Agi")
@Feature("Busca de Artigos")
@Story("Busca com resultado")
@Severity(SeverityLevel.BLOCKER)
@DisplayName("Busca com termo válido exibe ao menos um resultado")
```

### Online report

The latest report is published to GitHub Pages after every push to `main`:

**[https://thitoribeiro.github.io/evertec-agibank-ui-test-automation-playwright-java/](https://thitoribeiro.github.io/evertec-agibank-ui-test-automation-playwright-java/)**

---

## GitHub Actions CI/CD

### Triggers

| Event | Branch | Action |
|---|---|---|
| `push` | `main`, `develop` | Run tests → publish report to GitHub Pages |
| `pull_request` | `main` | Run tests → upload report as downloadable artifact |
| `workflow_dispatch` | any | Manual trigger from the GitHub Actions UI |

### Pipeline steps

```
1.  Checkout repository
2.  Set up Java 17 (Temurin) with Maven dependency cache
3.  Install Playwright Chromium + system dependencies
4.  Run UI test suite        (continue-on-error — report is always generated)
5.  Checkout gh-pages branch for Allure history (enables trend charts)
6.  Generate Allure Report   (with history from previous runs)
7.  Upload report as artifact (retained 30 days, named allure-report-<run>)
8.  Deploy report to GitHub Pages  (main branch only)
9.  Write test summary table to the Actions job summary page
```

### Enabling GitHub Pages (one-time setup)

1. Go to **Settings → Pages** in your repository.
2. Set **Source** to `Deploy from a branch`.
3. Set **Branch** to `gh-pages` / `/ (root)`.
4. After the first successful push to `main`, the report will be live at the URL above.

### Downloading a report artifact (PR runs)

1. Open the **Actions** tab in GitHub.
2. Click any workflow run.
3. Scroll to the **Artifacts** section.
4. Download `allure-report-<run-number>` and unzip to view offline.

---

## Branch Strategy

```
main         ← stable, always green — Allure report published to GitHub Pages
develop      ← integration branch for feature merges
feature/*    ← individual features and bug fixes
```

### Commit convention

| Prefix | Usage |
|---|---|
| `feat:` | New feature or test scenario |
| `fix:` | Bug fix in test or page object |
| `test:` | New or updated test cases |
| `build:` | Build system, dependencies, Docker |
| `ci:` | GitHub Actions configuration |
| `chore:` | Maintenance, refactoring, docs |

### Creating a feature branch

```bash
git checkout main && git pull origin main
git checkout -b feature/my-feature
# ... make changes, run tests locally ...
git push -u origin feature/my-feature
# Open PR: feature/my-feature → main
```

---

## License

This project is licensed under the [MIT License](LICENSE).
