#!/bin/bash
set -e
docker network create ecommerce-net

echo "Starting containers..."

docker-compose -f "./db-services/builder/docker-compose.yml" up -d
sleep 5
docker-compose -f "./eureka-server/builder/docker-compose.yml" up -d
sleep 10
docker-compose -f "./product-service/builder/docker-compose.yml" up -d

docker-compose -f "./operations-service/builder/docker-compose.yml" up -d

echo "✅ All containers started successfully."
