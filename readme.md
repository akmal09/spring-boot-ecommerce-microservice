1. First, create docker network with name "db-ecommerce-net" in local computer or in server.
2. Make sure the there are database who can be connected to every java service. If there is no database postgresql in the computer then execute docker-compose file inside folder builder in db_services folder.
3. Build the jar using runner.bar in every folder.
4. For initiation in local, just execute every docker-compose in every builder folder inside every module.
5. For initiation in vm, please execute "sh init-service.sh". (make sure docker and docker-compose are installed)