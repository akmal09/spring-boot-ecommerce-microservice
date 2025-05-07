[//]: # (This is for docker explanation)
NOTE:
Important command:
git diff --name-only

For Docker Command:
docker network connect db-ecommerce-net eureka-server
docker build -t akmal23/product-service:1.0.0 .
docker run --name product-service -d -p 8081:8081 --network db-ecommerce-net akmal23/product-service:1.0.0