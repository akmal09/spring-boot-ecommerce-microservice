@REM docker-compose --project-name "eureka-server" --down
@REM docker-compose --project-name "eureka-server" -f ./builder/docker-compose.yml up --build -d
@echo off
echo update latest repository
git pull origin development-2

timeout /t 3 /nobreak
call build.bat || exit /b 1

echo add jar to server
scp -i C:\Users\Akmal.Suranta\.ssh\ecommerce-project-microservice target\product-service-1.0.0.jar azureuser@20.247.180.27:/home/azureuser/ecommerce_builder/product-service

echo push code to repository
timeout /t 5 /nobreak
git push origin development-2