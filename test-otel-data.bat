@echo off
echo ========================================
echo Testing OpenTelemetry Data Generation
echo ========================================
echo.

echo Starting comprehensive test...
echo.

echo 1. Testing health endpoint...
curl -s -w "Status: %%{http_code}, Time: %%{time_total}s\n" http://localhost:8081/actuator/health
echo.

echo 2. Testing get products (should create traces)...
curl -s -w "Status: %%{http_code}, Time: %%{time_total}s\n" http://localhost:8081/external/product
echo.

echo 3. Testing get checkout (should create traces)...
curl -s -w "Status: %%{http_code}, Time: %%{time_total}s\n" http://localhost:8081/external/product/checkout
echo.

echo 4. Testing error scenario (404)...
curl -s -w "Status: %%{http_code}, Time: %%{time_total}s\n" http://localhost:8081/nonexistent
echo.

echo 5. Generating multiple requests for better data...
for /l %%i in (1,1,5) do (
    echo Request %%i/5...
    curl -s http://localhost:8081/external/product > nul
    timeout /t 1 > nul
)
echo.

echo ========================================
echo Test completed! 
echo.
echo Check your data:
echo - Jaeger UI: http://localhost:16686
echo - Prometheus: http://localhost:9090
echo - Collector logs: docker-compose -f docker-compose-observability.yml logs otel-collector
echo ========================================
echo.

set /p open="Open Jaeger UI now? (y/n): "
if /i "%open%"=="y" start http://localhost:16686

pause