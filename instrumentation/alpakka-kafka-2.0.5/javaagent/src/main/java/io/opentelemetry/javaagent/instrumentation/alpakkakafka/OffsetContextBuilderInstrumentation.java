/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.alpakkakafka;

import static io.opentelemetry.javaagent.instrumentation.alpakkakafka.KafkaConsumerTracer.tracer;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import akka.kafka.ConsumerMessage;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class OffsetContextBuilderInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    System.out.println("Creating type matcher for OffsetContextBuilder");
    return named("akka.kafka.internal.SourceWithOffsetContext$$anon$2");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    System.out.println("transforming OffsetContextBuilder: " + transformer);
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("createMessage"))
            .and(takesArgument(0, named("org.apache.kafka.clients.consumer.ConsumerRecord")))
            .and(returns(named("scala.Tuple2"))),
        OffsetContextBuilderInstrumentation.class.getName() + "$OffsetContextBuilderAdvice");
  }

  public static class OffsetContextBuilderAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void wrap(
        @Advice.Argument(value = 0, readOnly = false) ConsumerRecord<?, ?> record) {
      System.out.println("OffsetContextBuilder.beforeInvoke: " + record);
    }

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void wrap(
        @Advice.Return(readOnly = false)
            scala.Tuple2<ConsumerRecord<?, ?>, ConsumerMessage.CommittableOffsetMetadata> message) {
      System.out.println("OffsetContextBuilder.afterInvoke.1: " + message);
      try {
        // TODO where we need to close scope?
        Context traceContext = tracer().startSpan(message._1);
        Scope scope = traceContext.makeCurrent();
        TracingCommittableOffsetMetadata context =
            new TracingCommittableOffsetMetadata(message._2, traceContext, scope);
        message = message.copy(message._1, context);
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println("OffsetContextBuilder.afterInvoke.2: " + message);
    }
  }
}
