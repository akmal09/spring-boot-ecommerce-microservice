docker-compose --project-name "product-service" --down
docker-compose --project-name "product-service" -f ./builder/docker-compose.yml up --build -d