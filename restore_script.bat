@echo off
set CONTAINER_NAME=db-ecommerce-akmal
set POSTGRES_USER=ecommerce-akmal
set POSTGRES_PASSWORD=akmal2121234
set POSTGRES_DB=ecommerce_akmal
set BACKUP_FILE=db-ecommerce-akmal_backup.sql

@REM echo 🔄 Starting PostgreSQL container...
@REM docker run --name %CONTAINER_NAME% ^
@REM  -e POSTGRES_USER=%POSTGRES_USER% ^
@REM  -e POSTGRES_PASSWORD=%POSTGRES_PASSWORD% ^
@REM  -e POSTGRES_DB=%POSTGRES_DB% ^
@REM  -p 4000:5432 -d postgres:13.1-alpine

@REM echo 🕒 Waiting for container to initialize...
@REM timeout /t 5 /nobreak >nul

echo 📂 Copying SQL backup into container...
docker cp %BACKUP_FILE% %CONTAINER_NAME%:/tmp/%BACKUP_FILE%

echo ♻️ Restoring database...
docker exec -e PGPASSWORD=%POSTGRES_PASSWORD% -t %CONTAINER_NAME% ^
 psql -U %POSTGRES_USER% -d %POSTGRES_DB% -f /tmp/%BACKUP_FILE%

echo ✅ Done!