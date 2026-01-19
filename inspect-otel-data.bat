@echo off
echo ========================================
echo OpenTelemetry Data Inspection Tool
echo ========================================
echo.

:menu
echo Choose an option:
echo 1. Check if services are running
echo 2. View Jaeger traces (opens browser)
echo 3. View Prometheus metrics (opens browser)
echo 4. Check OTEL Collector logs
echo 5. Test operations-service endpoints
echo 6. View collector config
echo 7. Exit
echo.
set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" goto check_services
if "%choice%"=="2" goto open_jaeger
if "%choice%"=="3" goto open_prometheus
if "%choice%"=="4" goto check_logs
if "%choice%"=="5" goto test_endpoints
if "%choice%"=="6" goto view_config
if "%choice%"=="7" goto exit
goto menu

:check_services
echo.
echo Checking running services...
docker-compose -f docker-compose-observability.yml ps
echo.
echo Checking if operations-service is running...
curl -s http://localhost:8081/actuator/health > nul
if %errorlevel%==0 (
    echo ✓ Operations service is running
) else (
    echo ✗ Operations service is not running
    echo   Start it with: cd operations-service && mvnw spring-boot:run -Dspring.profiles.active=local
)
echo.
pause
goto menu

:open_jaeger
echo Opening Jaeger UI...
start http://localhost:16686
echo.
echo Jaeger UI opened in browser. Look for 'operations-service' traces.
echo.
pause
goto menu

:open_prometheus
echo Opening Prometheus UI...
start http://localhost:9090
echo.
echo Prometheus UI opened. Try queries like:
echo - http_server_requests_seconds_count
echo - jvm_memory_used_bytes
echo.
pause
goto menu

:check_logs
echo.
echo Checking OTEL Collector logs...
docker-compose -f docker-compose-observability.yml logs otel-collector --tail=50
echo.
pause
goto menu

:test_endpoints
echo.
echo Testing operations-service endpoints...
echo.
echo 1. Testing health endpoint...
curl -s http://localhost:8081/actuator/health
echo.
echo.
echo 2. Testing get products endpoint...
curl -s http://localhost:8081/external/product
echo.
echo.
echo 3. Testing get checkout endpoint...
curl -s http://localhost:8081/external/product/checkout
echo.
echo.
echo These requests should generate traces in Jaeger!
echo.
pause
goto menu

:view_config
echo.
echo Current OTEL Collector Configuration:
echo =====================================
type otel-collector-config.yaml
echo.
echo =====================================
echo.
pause
goto menu

:exit
echo Goodbye!
exit /b 0