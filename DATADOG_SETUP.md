# Datadog Integration Setup Guide

## Prerequisites
1. Datadog account with 14-day free trial
2. Docker and Docker Compose installed
3. Maven installed

## Setup Steps

### 1. Get Datadog API Keys
1. Login to your Datadog account
2. Go to Organization Settings > API Keys
3. Create a new API key and copy it
4. Go to Organization Settings > Application Keys
5. Create a new Application key and copy it

### 2. Configure Environment
1. Copy `.env.example` to `.env`
2. Replace `your-datadog-api-key-here` with your actual API key
3. Replace `your-datadog-app-key-here` with your actual Application key

### 3. Build Services
```bash
# Build all services
mvn clean package -DskipTests

# Or build individually
cd product-service && mvn clean package -DskipTests
cd ../operations-service && mvn clean package -DskipTests
cd ../eureka-server && mvn clean package -DskipTests
```

### 4. Start Services with Datadog
```bash
# Start all services including Datadog agent
docker-compose -f docker-compose-datadog.yml up -d

# Check if services are running
docker-compose -f docker-compose-datadog.yml ps
```

### 5. Verify Setup
- Eureka Server: http://localhost:8761
- Product Service: http://localhost:8081/actuator/health
- Operations Service: http://localhost:8082/actuator/health
- Product Service Metrics: http://localhost:8081/actuator/metrics

## Stress Testing

### Run Stress Tests
```bash
# Windows
stress-test.bat

# Or manually test individual endpoints:
curl "http://localhost:8081/stress/cpu?iterations=500"
curl "http://localhost:8081/stress/memory?sizeMB=50"
curl "http://localhost:8082/stress/checkout?orders=20"
```

### Available Stress Test Endpoints

#### Product Service (Port 8081)
- `/stress/cpu?iterations=N` - CPU intensive operations
- `/stress/memory?sizeMB=N` - Memory allocation stress
- `/stress/delay?delayMs=N` - Response time simulation
- `/stress/error?errorRate=N` - Error rate simulation (0-100%)

#### Operations Service (Port 8082)
- `/stress/checkout?orders=N` - Checkout process simulation
- `/stress/payment?payments=N` - Payment processing simulation
- `/stress/load?requests=N` - High load simulation

## Datadog Dashboard

### What to Monitor
1. **APM Traces**: Service-to-service calls, response times
2. **Metrics**: CPU, memory, request rates, error rates
3. **Logs**: Application logs from all services
4. **Infrastructure**: Container metrics, host metrics

### Key Metrics to Watch
- `http.request.duration` - Request response times
- `stress.test.requests` - Number of stress test requests
- `stress.test.errors` - Error count during stress tests
- `jvm.memory.used` - JVM memory usage
- `jvm.gc.time` - Garbage collection time

### Creating Dashboards
1. Go to Datadog > Dashboards > New Dashboard
2. Add widgets for:
   - Request Rate (requests/second)
   - Response Time (p95, p99)
   - Error Rate (%)
   - Memory Usage
   - CPU Usage

## Troubleshooting

### Common Issues
1. **Services not appearing in Datadog**
   - Check DD_API_KEY is correct
   - Verify datadog-agent container is running
   - Check service logs: `docker logs product-service`

2. **No traces appearing**
   - Ensure DD_TRACE_AGENT_PORT=8126 is set
   - Verify datadog-agent port 8126 is accessible
   - Check if Java agent is loaded in service logs

3. **Metrics not showing**
   - Verify actuator endpoints are enabled
   - Check micrometer-registry-datadog dependency
   - Ensure DD_API_KEY and DD_APP_KEY are set

### Useful Commands
```bash
# Check service logs
docker logs product-service
docker logs operations-service
docker logs datadog-agent

# Restart specific service
docker-compose -f docker-compose-datadog.yml restart product-service

# Stop all services
docker-compose -f docker-compose-datadog.yml down
```

## Next Steps
1. Create custom dashboards in Datadog
2. Set up alerts for high error rates or response times
3. Explore distributed tracing between services
4. Monitor database performance
5. Set up log analysis and alerting