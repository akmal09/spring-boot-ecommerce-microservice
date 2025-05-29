#!/bin/bash
set -e

# Check for Docker
if [ -x "$(command -v docker)" ]; then
  echo "✅ Docker is installed."
else
  echo "❌ Docker is not installed. Please install Docker before running this script."
fi

# Check for Docker Compose (v1 or v2 plugin)
if [ command -v docker-compose &> /dev/null ]; then
  echo "✅ docker-compose (v1) is installed."
else
  echo "❌ Docker Compose is not installed. Please install docker-compose or Docker Compose plugin."
  exit 1
fi

# EUREKA_JAR_DIR="./eureka-server/builder"
# if ls "$EUREKA_JAR_DIR"/*.jar &> /dev/null; then
#   echo "✅ Found .jar file in $EUREKA_JAR_DIR"
# else
#   echo "❌ No .jar file found in $EUREKA_JAR_DIR"
#   exit 1
# fi

# PRODUCT_JAR_DIR="./product-service/builder"
# if ls "$PRODUCT_JAR_DIR"/*.jar &> /dev/null; then
#   echo "✅ Found .jar file in $PRODUCT_JAR_DIR"
# else
#   echo "❌ No .jar file found in $PRODUCT_JAR_DIR"
#   exit 1
# fi

# OPERATION_JAR_DIR="./operations-service/builder"
# if ls "$OPERATION_JAR_DIR"/*.jar &> /dev/null; then
#   echo "✅ Found .jar file in $OPERATION_JAR_DIR"
# else
#   echo "❌ No .jar file found in $OPERATION_JAR_DIR"
#   exit 1
# fi

echo "✅ All required dependencies are installed."


docker network create db-ecommerce-net

echo "Starting containers..."

docker-compose -f "./db-services/builder/docker-compose.yml" up -d
sleep 5
docker-compose -f "./eureka-server/builder/docker-compose.yml" up -d
sleep 10
docker-compose -f "./product-service/builder/docker-compose.yml" up -d

docker-compose -f "./operations-service/builder/docker-compose.yml" up -d

echo "✅ All containers started successfully."
