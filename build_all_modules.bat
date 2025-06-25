@echo off

REM Build eureka-server module
cd eureka-server
call .\build_module.bat
if errorlevel 1 (
    echo Error building eureka-server module
    exit /b %errorlevel%
)
cd ..

REM Build operations-service module
cd operations-service
call .\build_module.bat
if errorlevel 1 (
    echo Error building operations-service module
    exit /b %errorlevel%
)
cd ..

REM Build product-service module
cd product-service
call .\build_module.bat
if errorlevel 1 (
    echo Error building product-service module
    exit /b %errorlevel%
)
cd ..

echo All modules built successfully. 