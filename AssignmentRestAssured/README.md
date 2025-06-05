📚 Bookstore API
Overview
This project is a simple Bookstore API built with FastAPI. It allows users to manage books and perform user authentication, including sign-up and login functionalities. The API uses JWT tokens for securing endpoints related to book management.

Features
Book Management: Create, update, delete, and retrieve books.

User Authentication: Includes user sign-up and login.

Secure Endpoints: Uses JWT for secure access.

Technologies
FastAPI

This project demonstrates REST API testing using **REST-assured**, **TestNG**, and **Maven**.

---

## 📁 Project Structure
AssignmentRestAssured/
├── src/
│ └── test/
│ └── java/
│ └── ... (Test Classes)
├── pom.xml
├── .github/
│ └── workflows/
│ └── ci.yml
└── README.md


---

## 🧪 How to Run Tests

Before running the tests, make sure you have:

- Java 11 or later installed  
- Apache Maven installed and configured  
- Internet connection to download Maven dependencies

Run your tests using:

```bash
mvn clean test


📊 Allure Reporting

✅ Step 1: Install Allure CLI

choco install allure

✅ Step 2: Run Tests & Generate Allure Report

# Run tests and generate results
mvn clean test

# Generate HTML report
allure generate target/allure-results --clean -o target/allure-report

# Open the report in your default browser
allure serve target/allure-results


✅ Allure Configuration
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-testng</artifactId>
    <version>2.13.9</version>
</dependency>

Add this plugin in your pom.xml:
<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.10.0</version>
</plugin>

🔁 GitHub Actions - CI/CD
.github/workflows/ci.yml


🛠 Workflow Configuration

name: REST-assured CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK
      uses: actions/setup-java@v3
      with:
        distribution: 'temurin'
        java-version: '11'

    - name: Build and Test
      run: mvn clean test

    - name: Generate Allure Report
      run: mvn io.qameta.allure:allure-maven:report

    - name: Upload Allure Report
      uses: actions/upload-artifact@v3
      with:
        name: allure-report
        path: target/site/allure-maven-plugin

🔧 Technologies Used

Java 11

Maven

REST-assured

TestNG

Allure Reporting

GitHub Actions for CI

📄 License
This project is licensed for educational/demo purpose

