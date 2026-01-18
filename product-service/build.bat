@echo off
echo ================================
echo Building config module...
echo ================================
cd ../config
call build.bat
if errorlevel 1 (
    echo Failed to build config module. Exiting...
    exit /b 1
)

echo ================================
echo Starting product-service...
echo ================================
cd ../product-service
mvn spring-boot:run
