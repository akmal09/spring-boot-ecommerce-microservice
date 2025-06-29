@echo off

docker exec -t db-ecommerce-akmal pg_dump -U db-ecommerce-akmal -d db-ecommerce-akmal -f /tmp/db-ecommerce-akmal_backup.sql

docker cp db-ecommerce-akmal:/tmp/alfitrah_backup.sql ./db-ecommerce-akmal_backup.sql


echo Done!