docker network create ecommerce-net

cd "../db-services"

docker-compose up


sleep 5

docker network connect ecommerce-net db-ecommerce-akmal