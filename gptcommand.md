please revised this bat script
@echo off
echo Running mvn clean install...
cd ../config
build.bat || exit /b 1
cd ../product-service
mvn spring-boot:run

o after the jar in config folder built, then it continues to the next process