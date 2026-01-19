@echo off
echo ========================================
echo Collected Data Inspector
echo ========================================
echo.

:menu
echo Data Inspection Options:
echo 1. Show Real-time Collector Logs
echo 2. Inspect Traces JSON File
echo 3. Check Metrics Data
echo 4. View Collector Statistics
echo 5. Test Data Flow End-to-End
echo 6. Export Data Summary
echo 7. Back to main menu
echo.
set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" goto realtime_logs
if "%choice%"=="2" goto inspect_traces
if "%choice%"=="3" goto check_metrics_data
if "%choice%"=="4" goto collector_stats
if "%choice%"=="5" goto test_flow
if "%choice%"=="6" goto export_summary
if "%choice%"=="7" goto exit
goto menu

:realtime_logs
echo.
echo Real-time Collector Logs (Press Ctrl+C to stop)...
echo ========================================
docker-compose -f docker-compose-otel-collector.yml logs -f otel-collector
echo.
pause
goto menu

:inspect_traces
echo.
echo Inspecting Traces JSON File...
echo.
if exist "otel-data\traces.json" (
    echo File size:
    for %%A in (otel-data\traces.json) do echo %%~zA bytes
    echo.
    echo Last modification:
    for %%A in (otel-data\traces.json) do echo %%~tA
    echo.
    echo Sample trace data (last 20 lines):
    echo ========================================
    powershell "Get-Content otel-data\traces.json | Select-Object -Last 20"
    echo ========================================
    echo.
    echo Searching for key trace elements...
    echo.
    echo Operations service traces:
    findstr /i "operations-service" otel-data\traces.json | wc -l
    echo.
    echo HTTP spans:
    findstr /i "http" otel-data\traces.json | wc -l
    echo.
    echo Database spans:
    findstr /i "jdbc\|sql\|postgres" otel-data\traces.json | wc -l
) else (
    echo ✗ No traces.json file found
    echo   Make sure:
    echo   1. Collector is running
    echo   2. Operations service is running
    echo   3. You've sent some test requests
)
echo.
pause
goto menu

:check_metrics_data
echo.
echo Checking Metrics Data...
echo.
echo 1. Collector Prometheus Metrics:
echo ========================================
curl -s http://localhost:8889/metrics | findstr "otel_" | head -10
echo.
echo 2. Operations Service Metrics:
echo ========================================
curl -s http://localhost:8081/actuator/prometheus | findstr "http_server_requests" | head -5
echo.
echo 3. Collector Internal Metrics:
echo ========================================
curl -s http://localhost:8888/metrics | findstr "otelcol_receiver" | head -5
echo ========================================
echo.
pause
goto menu

:collector_stats
echo.
echo Collector Statistics...
echo.
echo 1. Container Resource Usage:
docker stats otel-collector --no-stream
echo.
echo 2. Port Usage:
netstat -an | findstr ":4317\|:4318\|:8888\|:8889\|:13133"
echo.
echo 3. Collector Process Info:
docker exec otel-collector ps aux
echo.
pause
goto menu

:test_flow
echo.
echo Testing Complete Data Flow...
echo.
echo Step 1: Checking if services are running...
docker-compose -f docker-compose-otel-collector.yml ps | findstr otel-collector
curl -s http://localhost:8081/actuator/health > nul && echo "✓ Operations service running" || echo "✗ Operations service not running"
echo.
echo Step 2: Clearing old data...
if exist "otel-data\traces.json" del "otel-data\traces.json"
echo.
echo Step 3: Sending test requests...
echo Sending 3 test requests with 2 second intervals...
for /l %%i in (1,1,3) do (
    echo Request %%i: GET /external/product
    curl -s http://localhost:8081/external/product > nul
    timeout /t 2 > nul
)
echo.
echo Step 4: Waiting for data processing...
timeout /t 5 > nul
echo.
echo Step 5: Checking results...
if exist "otel-data\traces.json" (
    echo ✓ Traces file created
    for %%A in (otel-data\traces.json) do echo   File size: %%~zA bytes
    echo.
    echo Sample trace data:
    powershell "Get-Content otel-data\traces.json | Select-Object -Last 5"
) else (
    echo ✗ No traces file created
    echo   Check collector logs for errors
)
echo.
echo Step 6: Checking collector logs for our requests...
docker-compose -f docker-compose-otel-collector.yml logs --tail=20 otel-collector | findstr "operations-service\|traces\|spans"
echo.
pause
goto menu

:export_summary
echo.
echo Exporting Data Summary...
echo.
echo Creating summary report...
(
    echo OpenTelemetry Collector Data Summary
    echo Generated: %date% %time%
    echo =====================================
    echo.
    echo Collector Status:
    docker-compose -f docker-compose-otel-collector.yml ps
    echo.
    echo Data Files:
    if exist "otel-data" (
        dir otel-data
    ) else (
        echo No data directory found
    )
    echo.
    echo Recent Collector Logs:
    docker-compose -f docker-compose-otel-collector.yml logs --tail=10 otel-collector
    echo.
    echo Metrics Sample:
    curl -s http://localhost:8889/metrics | head -10
) > otel-summary.txt
echo.
echo ✓ Summary exported to otel-summary.txt
echo.
pause
goto menu

:exit
echo Returning to main menu...
goto :eof