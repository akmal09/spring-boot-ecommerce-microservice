# OpenTelemetry Collector Verification Guide

## 🎯 Goal
Verify that your OpenTelemetry SDK agent successfully sends data to the OTEL Collector before moving to LGTM stack.

## 📋 Step-by-Step Verification

### Step 1: Start the Collector
```bash
# Start only the collector (no backends yet)
docker-compose -f docker-compose-otel-collector.yml up -d

# Check if it's running
docker-compose -f docker-compose-otel-collector.yml ps
```

**Expected Result**: Container `otel-collector` should be running and healthy.

### Step 2: Verify Collector Health
```bash
# Check health endpoint
curl http://localhost:13133/

# Check if ports are open
netstat -an | findstr ":4317"  # OTLP gRPC
netstat -an | findstr ":4318"  # OTLP HTTP
```

**Expected Result**: 
- Health endpoint returns 200 OK
- Ports 4317 and 4318 are listening

### Step 3: Start Your Operations Service
```bash
cd operations-service
./mvnw spring-boot:run -Dspring.profiles.active=local
```

**Expected Result**: Service starts and connects to collector at `localhost:4317`

### Step 4: Send Test Data
```bash
# Send some requests to generate telemetry
curl http://localhost:8081/external/product
curl http://localhost:8081/external/product/checkout
curl http://localhost:8081/actuator/health
```

### Step 5: Verify Data Collection

#### A. Check Collector Logs
```bash
docker-compose -f docker-compose-otel-collector.yml logs otel-collector
```

**Look for**:
```
2024-01-19T10:30:15.123Z	info	TracesExporter	{"kind": "exporter", "data_type": "traces", "name": "logging", "traces": 1, "spans": 3}
2024-01-19T10:30:15.124Z	debug	loggingexporter	{"kind": "exporter", "data_type": "traces", "name": "logging", "resource spans": 1, "spans": 3}
```

#### B. Check Data Files
```bash
# Check if traces are being saved
ls -la otel-data/
cat otel-data/traces.json | tail -10
```

**Look for**: JSON data with trace information from your service

#### C. Check Metrics
```bash
# Collector internal metrics
curl http://localhost:8888/metrics | grep otelcol_receiver

# Exported metrics
curl http://localhost:8889/metrics | grep otel
```

## 🔍 What to Look For

### ✅ Success Indicators

#### In Collector Logs:
```
info	otlpreceiver	{"kind": "receiver", "name": "otlp", "data_type": "traces", "msg": "Starting OTLP receiver"}
info	TracesExporter	{"kind": "exporter", "data_type": "traces", "name": "logging", "traces": 1, "spans": 5}
debug	loggingexporter	Resource SchemaURL: 
debug	loggingexporter	Resource attributes:
     -> service.name: Str(operations-service)
     -> service.version: Str(1.0.0)
```

#### In Data Files:
```json
{
  "resourceSpans": [{
    "resource": {
      "attributes": [{
        "key": "service.name",
        "value": {"stringValue": "operations-service"}
      }]
    },
    "scopeSpans": [{
      "spans": [{
        "name": "GET /external/product",
        "kind": "SPAN_KIND_SERVER",
        "attributes": [{
          "key": "http.method",
          "value": {"stringValue": "GET"}
        }]
      }]
    }]
  }]
}
```

#### In Metrics:
```
# Collector received data
otelcol_receiver_accepted_spans_total{receiver="otlp"} 15
otelcol_receiver_accepted_metric_points_total{receiver="otlp"} 45

# Your service metrics
http_server_requests_seconds_count{method="GET",uri="/external/product"} 3
```

### ❌ Failure Indicators

#### Connection Issues:
```
error	otlpexporter	{"kind": "exporter", "data_type": "traces", "name": "otlp", "error": "failed to export traces: connection refused"}
```

#### No Data:
```
# Empty or missing files
ls otel-data/  # No files
# OR
cat otel-data/traces.json  # Empty file
```

#### Service Not Sending:
```
# No spans in collector logs
# No otelcol_receiver metrics increasing
```

## 🛠️ Troubleshooting

### Problem: No data in collector
**Check**:
1. Operations service configuration points to `localhost:4317`
2. Operations service is actually running
3. You've sent HTTP requests to the service
4. Collector is listening on port 4317

### Problem: Collector not starting
**Check**:
1. Docker is running
2. Port 4317 is not used by another service
3. Configuration file syntax is correct

### Problem: Data not being saved to files
**Check**:
1. `otel-data` directory permissions
2. Collector configuration has file exporter enabled
3. Disk space available

## 🎯 Success Criteria

Before moving to LGTM stack, you should see:

✅ **Collector receives data**: Logs show "TracesExporter" and "spans" messages  
✅ **Data is structured**: JSON files contain proper OpenTelemetry format  
✅ **Service identification**: `service.name: operations-service` in data  
✅ **HTTP spans**: Spans for your REST endpoints  
✅ **Database spans**: JDBC spans for database queries  
✅ **Custom spans**: Your business logic spans with attributes  
✅ **Metrics flowing**: HTTP request metrics and JVM metrics  

## 📊 Quick Verification Commands

```bash
# All-in-one verification
verify-otel-collector.bat

# Or manual checks:
curl http://localhost:13133/                                    # Health
docker logs otel-collector | grep -i "traces\|spans"          # Data flow
curl http://localhost:8081/external/product                    # Generate data
cat otel-data/traces.json | jq '.resourceSpans[0].resource'   # Verify structure
```

## 🚀 Next Steps

Once verification is complete:
1. ✅ Collector receives and processes data correctly
2. ✅ Data structure is valid OpenTelemetry format  
3. ✅ All telemetry types (traces, metrics, logs) are flowing
4. 🎯 **Ready for LGTM Stack Integration!**

The collector is now proven to work and ready to send data to Loki, Grafana, Tempo, and Mimir!