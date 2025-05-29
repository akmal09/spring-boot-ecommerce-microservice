#!/bin/bash
set -e
docker network create ecommerce-net

echo "Starting containers..."

MODULES=("db-services" "eureka-server" "product-service" "operation-service")

for MODULE in "${MODULES[@]}"; do
  echo "--------------------------------------"
  echo "Starting services in: $MODULE"
  echo "--------------------------------------"
  docker-compose -f "$MODULE/docker-compose.yml" up -d
done

echo ""
echo "✅ All containers started successfully."
