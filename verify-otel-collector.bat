@echo off
echo ========================================
echo OpenTelemetry Collector Verification
echo ========================================
echo.

:menu
echo Choose verification step:
echo 1. Start OTEL Collector
echo 2. Check Collector Health
echo 3. View Collector Logs
echo 4. Test Data Collection
echo 5. View Collected Data Files
echo 6. Check Collector Metrics
echo 7. Stop Collector
echo 8. Clean Up Data
echo 9. Exit
echo.
set /p choice="Enter your choice (1-9): "

if "%choice%"=="1" goto start_collector
if "%choice%"=="2" goto check_health
if "%choice%"=="3" goto view_logs
if "%choice%"=="4" goto test_collection
if "%choice%"=="5" goto view_data
if "%choice%"=="6" goto check_metrics
if "%choice%"=="7" goto stop_collector
if "%choice%"=="8" goto cleanup
if "%choice%"=="9" goto exit
goto menu

:start_collector
echo.
echo Starting OpenTelemetry Collector...
docker-compose -f docker-compose-otel-collector.yml up -d
echo.
echo Waiting for collector to start...
timeout /t 5 > nul
echo.
echo Collector started! Check status with option 2.
echo.
pause
goto menu

:check_health
echo.
echo Checking Collector Health...
echo.
echo 1. Container Status:
docker-compose -f docker-compose-otel-collector.yml ps
echo.
echo 2. Health Check:
curl -s http://localhost:13133/ && echo "✓ Health check passed" || echo "✗ Health check failed"
echo.
echo 3. OTLP Endpoints:
echo Testing OTLP gRPC (4317)...
netstat -an | findstr :4317 && echo "✓ OTLP gRPC port open" || echo "✗ OTLP gRPC port not available"
echo Testing OTLP HTTP (4318)...
netstat -an | findstr :4318 && echo "✓ OTLP HTTP port open" || echo "✗ OTLP HTTP port not available"
echo.
pause
goto menu

:view_logs
echo.
echo Viewing Collector Logs (last 50 lines)...
echo ========================================
docker-compose -f docker-compose-otel-collector.yml logs --tail=50 otel-collector
echo ========================================
echo.
pause
goto menu

:test_collection
echo.
echo Testing Data Collection...
echo.
echo Make sure your operations-service is running first!
echo Starting test in 3 seconds...
timeout /t 3 > nul
echo.
echo Sending test requests to operations-service...
echo.
echo 1. Health check:
curl -s -w "Status: %%{http_code}, Time: %%{time_total}s\n" http://localhost:8081/actuator/health
echo.
echo 2. Get products:
curl -s -w "Status: %%{http_code}, Time: %%{time_total}s\n" http://localhost:8081/external/product
echo.
echo 3. Get checkout:
curl -s -w "Status: %%{http_code}, Time: %%{time_total}s\n" http://localhost:8081/external/product/checkout
echo.
echo Test requests sent! Check collector logs (option 3) to see if data was received.
echo.
pause
goto menu

:view_data
echo.
echo Viewing Collected Data Files...
echo.
echo Checking if data directory exists...
if exist "otel-data" (
    echo ✓ Data directory found
    echo.
    echo Files in otel-data directory:
    dir otel-data /b
    echo.
    echo Checking traces.json file...
    if exist "otel-data\traces.json" (
        echo ✓ Traces file found
        echo.
        echo Last few lines of traces.json:
        echo ========================================
        powershell "Get-Content otel-data\traces.json | Select-Object -Last 10"
        echo ========================================
    ) else (
        echo ✗ No traces.json file found - no data collected yet
    )
) else (
    echo ✗ Data directory not found - collector may not be running
)
echo.
pause
goto menu

:check_metrics
echo.
echo Checking Collector Metrics...
echo.
echo 1. Collector Internal Metrics:
curl -s http://localhost:8888/metrics | findstr "otelcol" | head -5
echo.
echo 2. Prometheus Metrics Endpoint:
curl -s http://localhost:8889/metrics | findstr "otel" | head -5
echo.
echo 3. ZPages (Collector Debug Info):
echo Opening http://localhost:55679 in browser...
start http://localhost:55679
echo.
pause
goto menu

:stop_collector
echo.
echo Stopping OpenTelemetry Collector...
docker-compose -f docker-compose-otel-collector.yml down
echo.
echo Collector stopped.
echo.
pause
goto menu

:cleanup
echo.
echo Cleaning up collected data...
if exist "otel-data" (
    rmdir /s /q otel-data
    echo ✓ Data directory cleaned
) else (
    echo ✓ No data directory to clean
)
echo.
echo Removing Docker volumes...
docker volume prune -f
echo.
echo Cleanup completed.
echo.
pause
goto menu

:exit
echo.
echo Verification script completed!
echo.
echo Quick reference:
echo - Collector UI: http://localhost:55679
echo - Health check: http://localhost:13133
echo - Metrics: http://localhost:8889/metrics
echo - Data files: ./otel-data/
echo.
exit /b 0