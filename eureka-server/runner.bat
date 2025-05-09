@REM docker-compose --project-name "eureka-server" --down
@REM docker-compose --project-name "eureka-server" -f ./builder/docker-compose.yml up --build -d
echo "update latest repository"
git pull origin publish
timeout /t 3 /nobreak
echo "build and test"
mvn clean install

scp -i C:\Users\Akmal.Suranta\.ssh\id_ed25519 target/eureka-server-1.0.0.jar azureuser@20.247.180.27:/home/azureuser/list_jar

timeout /t 5 /nobreak
git push origin publish
