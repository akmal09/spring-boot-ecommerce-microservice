docker-compose --project-name "eureka-server" --down
docker-compose --project-name "eureka-server" -f ./builder/docker-compose.yml up --build -d