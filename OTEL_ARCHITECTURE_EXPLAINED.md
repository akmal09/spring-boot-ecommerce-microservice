# OpenTelemetry Architecture Explained

## 🏗️ Component Breakdown

### 1. **Your Application (operations-service)**
```java
// Generates telemetry data
Span span = tracer.spanBuilder("get-products").startSpan();
// This creates trace data in OTLP format
```
- **Generates**: Traces, metrics, logs
- **Format**: OTLP (OpenTelemetry Protocol)
- **Sends to**: OTEL Collector (port 4317)

### 2. **OpenTelemetry Collector** 
```yaml
# otel-collector-config.yaml
receivers:
  otlp:                    # Receives OTLP data from your app
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317

exporters:
  jaeger:                  # Sends traces to Jaeger
    endpoint: jaeger:14250
  prometheus:              # Exposes metrics for Prometheus
    endpoint: "0.0.0.0:8889"
```
- **Receives**: OTLP data from your app
- **Processes**: Batching, filtering, enrichment
- **Exports**: To multiple backends (Jaeger, Prometheus, etc.)

### 3. **Jaeger** (Tracing Backend)
- **Receives**: Trace data from OTEL Collector
- **Stores**: Traces in its database
- **Provides**: Web UI to visualize traces
- **Port**: 16686 (UI), 14250 (receives from collector)

### 4. **Prometheus** (Metrics Backend)
- **Scrapes**: Metrics from OTEL Collector
- **Stores**: Time-series metrics data
- **Provides**: Query interface and API
- **Port**: 9090 (UI), 8889 (collector metrics endpoint)

## 🔄 Complete Data Flow

```
┌─────────────────┐    OTLP/gRPC     ┌──────────────────┐
│                 │    (port 4317)   │                  │
│ operations-     ├─────────────────►│ OTEL Collector   │
│ service         │                  │                  │
│ (your app)      │                  │ • Receives OTLP  │
└─────────────────┘                  │ • Processes      │
                                     │ • Routes data    │
                                     └─────────┬────────┘
                                               │
                              ┌────────────────┼────────────────┐
                              │                │                │
                              ▼                ▼                ▼
                    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
                    │   Jaeger    │  │ Prometheus  │  │   Console   │
                    │             │  │             │  │    Logs     │
                    │ • Stores    │  │ • Scrapes   │  │             │
                    │   traces    │  │   metrics   │  │ • Debug     │
                    │ • UI at     │  │ • UI at     │  │   output    │
                    │   :16686    │  │   :9090     │  │             │
                    └─────────────┘  └─────────────┘  └─────────────┘
```

## 🎯 Why This Architecture?

### **Decoupling Benefits**:
1. **Your app** doesn't need to know about Jaeger/Prometheus directly
2. **OTEL Collector** can route to multiple backends
3. **Easy to switch** backends without changing your app
4. **Processing** happens outside your app (better performance)

### **What Each Component Handles**:

| Component | Traces | Metrics | Logs | UI | Storage |
|-----------|--------|---------|------|----|---------| 
| Your App | ✅ Generates | ✅ Generates | ✅ Generates | ❌ | ❌ |
| OTEL Collector | ✅ Routes | ✅ Routes | ✅ Routes | ❌ | ❌ |
| Jaeger | ✅ Stores/Views | ❌ | ❌ | ✅ | ✅ |
| Prometheus | ❌ | ✅ Stores/Views | ❌ | ✅ | ✅ |

## 🔍 In Our Docker Compose

```yaml
# Your app sends OTLP data here
otel-collector:
  ports:
    - "4317:4317"   # ← Your app connects here
  depends_on:
    - jaeger        # ← Collector sends traces here

# Jaeger receives traces from collector
jaeger:
  ports:
    - "16686:16686" # ← You view traces here
    - "14250:14250" # ← Collector sends traces here

# Prometheus scrapes metrics from collector  
prometheus:
  ports:
    - "9090:9090"   # ← You view metrics here
```

## 🚀 Alternative Architectures

### **Direct to Jaeger** (simpler, but less flexible):
```
Your App → Jaeger directly
```

### **Multiple Collectors** (enterprise):
```
Your App → OTEL Collector → OTEL Collector → Backends
           (edge)           (central)
```

### **Cloud Native** (production):
```
Your App → OTEL Collector → Cloud Provider
           (sidecar)        (AWS X-Ray, GCP Trace, etc.)
```

## 🎯 Key Takeaway

- **OTEL Collector** = Data pipeline/router
- **Jaeger** = Tracing storage + UI  
- **Prometheus** = Metrics storage + UI
- **Your App** = Data generator

The collector is the "middleman" that makes everything work together!