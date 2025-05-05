docker-compose --project-name "operations-service" --down
docker-compose --project-name "operations-service" -f ./builder/docker-compose.yml up --build -d