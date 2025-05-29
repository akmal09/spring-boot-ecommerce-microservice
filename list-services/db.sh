docker network create ecommerce-net

docker-compose -f "../db-services/docker-compose.yml" up

sleep 5

docker network connect ecommerce-net db-ecommerce-akmal