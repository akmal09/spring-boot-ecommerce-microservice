# OpenTelemetry Implementation Guide - Operations Service

## Overview

This service is fully instrumented with OpenTelemetry for comprehensive observability including:
- **Distributed Tracing**: Track requests across service boundaries
- **Metrics**: Performance and business metrics
- **Logging**: Structured logging with trace correlation

## What's Implemented

### 1. Automatic Instrumentation
- **HTTP requests/responses** - Spring WebMVC auto-instrumentation
- **Database queries** - JDBC instrumentation for PostgreSQL
- **External HTTP calls** - RestTemplate instrumentation

### 2. Manual Instrumentation
- **Custom spans** in controllers and services
- **Business metrics** and events
- **Error tracking** and exception recording
- **Custom attributes** for business context

### 3. Configuration
- **OTLP exporter** for sending data to observability backends
- **Sampling configuration** for production optimization
- **Service identification** with proper naming

## Key Features

### Tracing Examples

#### Controller Level Tracing
```java
@GetMapping
public ResponseEntity<?> getProductDirect(){
    Span span = tracer.spanBuilder("get-products-direct")
            .setAttribute("controller", "OperationsController")
            .setAttribute("method", "getProductDirect")
            .startSpan();
    
    try (Scope scope = span.makeCurrent()) {
        // Business logic
        span.addEvent("Starting product fetch");
        ResponseObject result = operationService.getProducts();
        span.setAttribute("response.status", result.getStatus());
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(200));
    } catch(Exception e) {
        span.recordException(e);
        span.setAttribute("error", true);
        throw e;
    } finally {
        span.end();
    }
}
```

#### Service Level Tracing
```java
public ResponseObject getProducts(){
    Span span = tracer.spanBuilder("operation-service.get-products")
            .setAttribute("service", "OperationService")
            .startSpan();
    
    try (Scope scope = span.makeCurrent()) {
        List<Product> products = productRepository.findAll();
        span.setAttribute("products.count", products.size());
        span.addEvent("Products retrieved successfully");
        return new ResponseObject("SUCCESS", products);
    } finally {
        span.end();
    }
}
```

### Trace Attributes Used

#### Standard Attributes
- `service.name`: operations-service
- `service.version`: 1.0.0
- `http.method`: GET, POST, etc.
- `http.status_code`: Response status codes

#### Custom Business Attributes
- `products.count`: Number of products retrieved
- `checkout.items.count`: Number of checkout items
- `payment.amount`: Payment amount
- `payment.transaction_id`: Transaction identifier
- `response.status`: Business response status

### Events and Milestones
- `Starting product fetch`
- `Products retrieved successfully`
- `Payment processing started`
- `External API call completed`

## Running with OpenTelemetry

### Local Development

1. **Start OTEL Collector** (optional, for local testing):
```bash
# Using Docker
docker run -p 4317:4317 -p 4318:4318 \
  otel/opentelemetry-collector-contrib:latest
```

2. **Run the service**:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=local
```

### Docker Environment

The service is configured to send telemetry data to:
- **OTLP Endpoint**: `${OTEL_EXPORTER_OTLP_ENDPOINT:http://localhost:4317}`
- **Headers**: `${OTEL_EXPORTER_OTLP_HEADERS:}` (for authentication)

### Environment Variables

```bash
# OpenTelemetry Configuration
OTEL_EXPORTER_OTLP_ENDPOINT=http://jaeger:4317
OTEL_EXPORTER_OTLP_HEADERS=api-key=your-api-key

# Service Information
OTEL_SERVICE_NAME=operations-service
OTEL_SERVICE_VERSION=1.0.0
```

## Observability Backends

### Supported Backends
- **Jaeger** - Distributed tracing
- **Zipkin** - Distributed tracing
- **Datadog** - Full observability platform
- **New Relic** - Application monitoring
- **Grafana + Tempo** - Open source stack

### Sample Jaeger Setup
```yaml
# docker-compose.yml
version: '3.8'
services:
  jaeger:
    image: jaegertracing/all-in-one:latest
    ports:
      - "16686:16686"  # Jaeger UI
      - "4317:4317"    # OTLP gRPC
      - "4318:4318"    # OTLP HTTP
    environment:
      - COLLECTOR_OTLP_ENABLED=true
```

## Monitoring Endpoints

### Health and Metrics
- **Health**: `http://localhost:8081/actuator/health`
- **Metrics**: `http://localhost:8081/actuator/metrics`
- **Prometheus**: `http://localhost:8081/actuator/prometheus`
- **Traces**: `http://localhost:8081/actuator/traces`

### Key Metrics to Monitor
- `http.server.requests` - HTTP request metrics
- `jdbc.connections.active` - Database connection pool
- `jvm.memory.used` - JVM memory usage
- `operations.products.retrieved` - Business metric

## Best Practices Implemented

### 1. Span Naming
- Use descriptive, hierarchical names
- Include service and method context
- Example: `operation-service.get-products`

### 2. Attributes
- Add business context attributes
- Include error information
- Use consistent naming conventions

### 3. Events
- Mark important milestones
- Add context for debugging
- Include timing information

### 4. Error Handling
- Always record exceptions
- Set error attributes
- Maintain span lifecycle

### 5. Performance
- Use sampling in production
- Avoid high-cardinality attributes
- Clean up resources properly

## Troubleshooting

### Common Issues

1. **No traces appearing**:
   - Check OTLP endpoint configuration
   - Verify network connectivity
   - Check sampling configuration

2. **High overhead**:
   - Reduce sampling rate
   - Limit span attributes
   - Check instrumentation configuration

3. **Missing context**:
   - Ensure proper scope management
   - Check async operation handling
   - Verify context propagation

### Debug Configuration
```yaml
logging:
  level:
    io.opentelemetry: DEBUG
    com.project.operations: DEBUG
```

## Next Steps

1. **Add custom metrics** for business KPIs
2. **Implement alerting** based on trace data
3. **Create dashboards** for service monitoring
4. **Set up distributed tracing** across all services
5. **Implement log correlation** with trace IDs