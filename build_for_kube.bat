@REM  Build every services to jar
call build_all_modules.bat || exit /b 1

@REM Build Docker images
docker build -t akmal23/eureka-server:latest ./eureka-server
docker build -t akmal23/product-service:latest ./product-service  
docker build -t akmal23/operations-service:latest ./operations-service

@REM Push to registry
docker push akmal23/eureka-server:latest
docker push akmal23/product-service:latest
docker push akmal23/operations-service:latest