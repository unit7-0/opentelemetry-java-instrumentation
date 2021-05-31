/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.alpakkakafka;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.Arrays;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class AlpakkaKafkaInstrumentationModule extends InstrumentationModule {
  public AlpakkaKafkaInstrumentationModule() {
    super("alpakka-kafka", "alpakka-kafka-2.0.5");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return Arrays.asList(new OffsetContextBuilderInstrumentation());
  }
}
