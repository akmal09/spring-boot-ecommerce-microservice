@echo off
echo Starting Datadog Stress Testing...
echo.

set PRODUCT_SERVICE_URL=http://localhost:8081
set OPERATIONS_SERVICE_URL=http://localhost:8082

echo Testing Product Service...
echo.

echo 1. CPU Stress Test
curl "%PRODUCT_SERVICE_URL%/stress/cpu?iterations=500"
echo.
echo.

echo 2. Memory Stress Test
curl "%PRODUCT_SERVICE_URL%/stress/memory?sizeMB=50"
echo.
echo.

echo 3. Delay Stress Test
curl "%PRODUCT_SERVICE_URL%/stress/delay?delayMs=2000"
echo.
echo.

echo 4. Error Stress Test
curl "%PRODUCT_SERVICE_URL%/stress/error?errorRate=30"
echo.
echo.

echo Testing Operations Service...
echo.

echo 5. Checkout Stress Test
curl "%OPERATIONS_SERVICE_URL%/stress/checkout?orders=20"
echo.
echo.

echo 6. Payment Stress Test
curl "%OPERATIONS_SERVICE_URL%/stress/payment?payments=15"
echo.
echo.

echo 7. Load Stress Test
curl "%OPERATIONS_SERVICE_URL%/stress/load?requests=200"
echo.
echo.

echo Testing Product Service Telemetry...
echo.

echo 8. Generate Product Traffic
curl "%PRODUCT_SERVICE_URL%/telemetry-test/generate-traffic?requests=20"
echo.
echo.

echo 9. Get Product Stats
curl "%PRODUCT_SERVICE_URL%/telemetry-test/product-stats"
echo.
echo.

echo 10. Test Product Endpoints
curl "%PRODUCT_SERVICE_URL%/internal/api/products"
echo.
curl "%PRODUCT_SERVICE_URL%/internal/api/products/1"
echo.
curl "%PRODUCT_SERVICE_URL%/internal/api/products/withresponseObject"
echo.
echo.

echo Stress testing completed!
echo Check your Datadog dashboard for metrics and traces.
pause