# OpenTelemetry SDK: The Built-in Agent

## 🤖 Yes! SDK = Agent Inside Your App

The OpenTelemetry SDK acts like an **intelligent agent** running inside your Java application that:

### **Automatic Collection** (Agent Behavior):
```java
// You don't write this code - the SDK agent does it automatically!

// When HTTP request comes in:
@GetMapping("/external/product")
public ResponseEntity<?> getProductDirect() {
    // SDK AGENT automatically:
    // 1. Creates HTTP span
    // 2. Records request method, URL, headers
    // 3. Tracks timing
    // 4. Records response status
    // 5. Sends to OTEL Collector
}

// When database query happens:
List<Product> products = productRepository.findAll();
// SDK AGENT automatically:
// 1. Creates JDBC span
// 2. Records SQL query
// 3. Tracks connection info
// 4. Records timing
// 5. Links to parent HTTP span
```

### **Manual Instrumentation** (You Control the Agent):
```java
// You can also direct the agent manually:
Span span = tracer.spanBuilder("custom-business-logic").startSpan();
try (Scope scope = span.makeCurrent()) {
    // Your business logic
    span.setAttribute("products.count", products.size()); // Agent records this
    span.addEvent("Processing completed");                // Agent records this
} finally {
    span.end(); // Agent sends this data to collector
}
```

## 🔄 Agent Data Flow

```
┌─────────────────────────────────────────────────────────────┐
│ Your Java Application (JVM Process)                        │
│                                                             │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ OpenTelemetry SDK Agent                                 │ │
│ │                                                         │ │
│ │ 🤖 Auto-Instrumentation:                               │ │
│ │ • HTTP requests/responses                               │ │
│ │ • Database queries (JDBC)                              │ │
│ │ • External HTTP calls                                  │ │
│ │ • JVM metrics                                          │ │
│ │                                                         │ │
│ │ 📊 Data Processing:                                     │ │
│ │ • Batching spans                                       │ │
│ │ • Adding context                                       │ │
│ │ • Sampling decisions                                   │ │
│ │                                                         │ │
│ │ 📤 Export to Collector:                                │ │
│ │ • OTLP protocol                                        │ │
│ │ • Port 4317                                            │ │
│ │ • Automatic retry                                      │ │
│ └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ OTLP Data
                                ▼
┌─────────────────────────────────────────────────────────────┐
│ OTEL Collector (Separate Process)                          │
│ • Receives data from SDK agent                             │
│ • Routes to Jaeger, Prometheus, etc.                      │
└─────────────────────────────────────────────────────────────┘
```

## 🎯 Agent Capabilities in Your Setup

### **1. HTTP Agent** (Spring WebMVC Instrumentation):
```yaml
# application-local.yml
otel:
  instrumentation:
    spring-webmvc:
      enabled: true  # ← Agent monitors all HTTP endpoints
```

**What the agent does automatically**:
- Creates spans for every HTTP request
- Records method, URL, status code, timing
- Links spans together for request flow

### **2. Database Agent** (JDBC Instrumentation):
```yaml
otel:
  instrumentation:
    jdbc:
      enabled: true  # ← Agent monitors all database calls
```

**What the agent does automatically**:
- Creates spans for every SQL query
- Records connection info, query text, timing
- Links database spans to HTTP request spans

### **3. HTTP Client Agent**:
```yaml
otel:
  instrumentation:
    http-clients:
      enabled: true  # ← Agent monitors outgoing HTTP calls
```

**What the agent does automatically**:
- Creates spans for RestTemplate calls
- Records external service calls
- Propagates trace context to downstream services

## 🔍 Agent in Action

### **When you call**: `curl http://localhost:8081/external/product`

**The SDK agent automatically**:
1. **Detects** incoming HTTP request
2. **Creates** root span: `GET /external/product`
3. **Records** HTTP method, URL, headers
4. **Starts** timing

**When your code calls**: `productRepository.findAll()`

**The SDK agent automatically**:
1. **Detects** JDBC call
2. **Creates** child span: `SELECT * FROM products`
3. **Records** SQL query, connection info
4. **Links** to parent HTTP span

**When request completes**:
1. **Records** response status, timing
2. **Batches** all spans together
3. **Sends** via OTLP to collector (port 4317)

## 🤖 Agent Configuration

### **In your pom.xml** (Agent Dependencies):
```xml
<!-- The agent libraries -->
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>  <!-- HTTP agent -->
</dependency>
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-jdbc</artifactId>                <!-- DB agent -->
</dependency>
```

### **In application-local.yml** (Agent Settings):
```yaml
otel:
  service:
    name: operations-service     # Agent identifies itself
  exporter:
    otlp:
      endpoint: http://localhost:4317  # Where agent sends data
  instrumentation:               # What the agent monitors
    spring-webmvc:
      enabled: true             # HTTP agent ON
    jdbc:
      enabled: true             # Database agent ON
    http-clients:
      enabled: true             # HTTP client agent ON
```

## 🎯 Key Point

**SDK = Smart Agent** that:
- ✅ **Lives inside** your application process
- ✅ **Automatically collects** telemetry data
- ✅ **Sends data** to OTEL Collector
- ✅ **Requires minimal code** changes
- ✅ **Works transparently** in background

**OTEL Collector = Data Router** that:
- ✅ **Receives data** from SDK agents
- ✅ **Processes and routes** to backends
- ✅ **Runs separately** from your app

So yes, you're absolutely right! The SDK is essentially an intelligent agent embedded in your application! 🎯