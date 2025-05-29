docker network create ecommerce-net
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$PROJECT_ROOT/db-services"

docker-compose up


sleep 5

docker network connect ecommerce-net db-ecommerce-akmal