@echo off

docker exec -t db-ecommerce-akmal pg_dump -U ecommerce-akmal -d ecommerce_akmal -f /tmp/db-ecommerce-akmal_backup.sql

docker cp db-ecommerce-akmal:/tmp/db-ecommerce-akmal_backup.sql ./db-ecommerce-akmal_backup.sql


echo Done!