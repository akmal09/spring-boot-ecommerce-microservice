package com.project.operations;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OpenTelemetryIntegrationTest {

    @Autowired
    private OpenTelemetry openTelemetry;

    @Autowired
    private Tracer tracer;

    @Test
    void contextLoads() {
        assertThat(openTelemetry).isNotNull();
        assertThat(tracer).isNotNull();
    }

    @Test
    void tracerHasCorrectInstrumentationScope() {
        assertThat(tracer.toString()).contains("operations-service");
    }
}