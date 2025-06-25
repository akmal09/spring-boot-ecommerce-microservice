#!/bin/bash
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print status messages
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️ $1${NC}"
}

# Function to check if a command exists
# check_command() {
#     if ! command -v $1 &> /dev/null; then
#         print_error "$1 is not installed. Please install it before running this script."
#         exit 1
#     fi
#     print_status "$1 is installed."
# }

# Function to check if a directory exists
check_directory() {
    if [ ! -d "$1" ]; then
        print_error "Directory $1 does not exist."
        exit 1
    fi
}

# Function to start a service
start_service() {
    local service_dir=$1
    local service_name=$2
    local wait_time=${3:-5}

    print_status "Starting $service_name..."
    cd "$service_dir"
    
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found in $service_dir"
        return 1
    fi

    docker-compose up --remove-orphans -d
    print_status "Waiting for $service_name to start..."
    sleep $wait_time
}

# Function to stop all services
stop_services() {
    print_status "Stopping all services..."
    
    for service_dir in */builder; do
        if [ -d "$service_dir" ] && [ -f "$service_dir/docker-compose.yml" ]; then
            print_status "Stopping service in $service_dir"
            cd "$service_dir"
            docker-compose down
            cd - > /dev/null
        fi
    done
    
    print_status "All services stopped."
}

# Check for required commands
# check_command docker
# check_command docker-compose

# Create network if it doesn't exist
if ! docker network ls | grep -q "db-ecommerce-net"; then
    print_status "Creating Docker network..."
    docker network create db-ecommerce-net
else
    print_status "Docker network already exists."
fi

# Handle command line arguments
case "$1" in
    "stop")
        stop_services
        exit 0
        ;;
    "restart")
        stop_services
        ;;
esac

# Start services in the correct order
print_status "Starting all services..."

# Start database services first
# start_service "db-services/builder" "Database Services" 10
cd db-services/builder
docker-compose up -d
cd ../..

# Start Eureka Server
# start_service "../../eureka-server/builder" "Eureka Server" 15
cd eureka-server/builder
docker-compose up --build -d
cd ../..

# Start Product Service
# start_service "../../product-service/builder" "Product Service" 10
cd product-service/builder
docker-compose up --build -d
cd ../..

# Start Operations Service
# start_service "../../operations-service/builder" "Operations Service" 10
cd operations-service/builder
docker-compose up --build -d
cd ../..

print_status "All services have been started successfully!"
print_status "You can check the status of your services using 'docker ps'"
print_status "To stop all services, run: ./init-service.sh stop"
