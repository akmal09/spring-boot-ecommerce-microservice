# OpenTelemetry Quick Start Guide

## 🚀 Quick Setup (5 minutes)

### Step 1: Start Observability Stack
```bash
# Start Jaeger, OTEL Collector, and Prometheus
docker-compose -f docker-compose-observability.yml up -d

# Check if services are running
docker-compose -f docker-compose-observability.yml ps
```

### Step 2: Start Operations Service
```bash
cd operations-service
./mvnw spring-boot:run -Dspring.profiles.active=local
```

### Step 3: Generate Some Data
```bash
# Test endpoints to generate traces
curl http://localhost:8081/external/product
curl http://localhost:8081/external/product/checkout
curl http://localhost:8081/actuator/health
```

### Step 4: View Your Data

#### 📊 Traces (Jaeger)
- **URL**: http://localhost:16686
- **Service**: Select "operations-service"
- **Look for**: HTTP requests, database queries, custom spans

#### 📈 Metrics (Prometheus)
- **URL**: http://localhost:9090
- **Try queries**:
  - `http_server_requests_seconds_count` - HTTP request counts
  - `jvm_memory_used_bytes` - JVM memory usage
  - `jdbc_connections_active` - Database connections

#### 🔍 Raw Data (Collector Logs)
```bash
# View collector processing logs
docker-compose -f docker-compose-observability.yml logs otel-collector
```

## 📋 What You'll See

### In Jaeger Traces:
1. **HTTP Spans**: Incoming requests to your controllers
2. **Service Spans**: Your custom business logic spans
3. **Database Spans**: JDBC queries to PostgreSQL
4. **Attributes**: Custom business data (product counts, etc.)
5. **Events**: Milestones in your business processes
6. **Errors**: Exception details when things go wrong

### In Prometheus Metrics:
1. **HTTP Metrics**: Request rates, response times, status codes
2. **JVM Metrics**: Memory, GC, thread usage
3. **Database Metrics**: Connection pool stats
4. **Custom Metrics**: Your business KPIs

## 🔧 Data Flow

```
Your App → OTEL SDK → OTEL Collector → Jaeger/Prometheus
```

1. **Your App**: Generates telemetry data using OTEL SDK
2. **OTEL SDK**: Collects and batches the data
3. **OTEL Collector**: Receives, processes, and routes data
4. **Jaeger**: Stores and visualizes traces
5. **Prometheus**: Stores and queries metrics

## 🐛 Troubleshooting

### No Traces in Jaeger?
```bash
# Check if collector is receiving data
docker-compose -f docker-compose-observability.yml logs otel-collector | grep "traces"

# Check if your app is sending data
curl http://localhost:8081/actuator/health
# Then check Jaeger UI for new traces
```

### No Metrics in Prometheus?
```bash
# Check if your app metrics endpoint works
curl http://localhost:8081/actuator/prometheus

# Check Prometheus targets
# Go to http://localhost:9090/targets
```

### Service Not Starting?
```bash
# Check application logs
cd operations-service
./mvnw spring-boot:run -Dspring.profiles.active=local

# Common issues:
# - Database not running (check PostgreSQL)
# - Port conflicts (8081 already in use)
# - OTEL collector not reachable
```

## 📊 Sample Queries

### Jaeger Queries:
- **Service**: operations-service
- **Operation**: All operations
- **Tags**: `error=true` (to find errors)
- **Lookback**: Last hour

### Prometheus Queries:
```promql
# Request rate
rate(http_server_requests_seconds_count[5m])

# Error rate
rate(http_server_requests_seconds_count{status=~"5.."}[5m])

# Response time percentiles
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# Database connections
jdbc_connections_active
```

## 🎯 What to Look For

### Successful Trace Should Show:
1. **Root Span**: HTTP request (e.g., `GET /external/product`)
2. **Service Span**: Your business logic (e.g., `operation-service.get-products`)
3. **Database Span**: JDBC query (e.g., `SELECT * FROM products`)
4. **Attributes**: Business context (product count, response status)
5. **Events**: Process milestones

### Sample Trace Structure:
```
GET /external/product (200ms)
├── get-products-direct (180ms)
    ├── operation-service.get-products (150ms)
        └── SELECT products (120ms)
```

## 🔄 Next Steps

Once you see data flowing correctly:

1. **Verify all endpoints** generate traces
2. **Check error scenarios** create proper error spans
3. **Confirm database queries** are captured
4. **Test external service calls** (when implemented)
5. **Move to LGTM stack** for production-ready observability

## 🛠️ Helper Script

Use the inspection tool:
```bash
# Windows
inspect-otel-data.bat

# Or manually check:
# 1. Services: docker-compose -f docker-compose-observability.yml ps
# 2. Jaeger: http://localhost:16686
# 3. Prometheus: http://localhost:9090
# 4. Test: curl http://localhost:8081/external/product
```

## 🎉 Success Criteria

You'll know it's working when:
- ✅ Jaeger shows traces for your HTTP requests
- ✅ Spans include your custom business attributes
- ✅ Database queries appear as child spans
- ✅ Prometheus shows HTTP and JVM metrics
- ✅ Error scenarios create spans with error=true
- ✅ Logs include trace and span IDs