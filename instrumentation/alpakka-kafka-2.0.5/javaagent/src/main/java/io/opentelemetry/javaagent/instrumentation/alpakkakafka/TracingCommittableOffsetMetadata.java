/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.alpakkakafka;

import akka.Done;
import akka.kafka.ConsumerMessage;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.util.concurrent.CompletionStage;
import scala.concurrent.Future;

public class TracingCommittableOffsetMetadata implements ConsumerMessage.CommittableOffsetMetadata {

  private final ConsumerMessage.CommittableOffsetMetadata delegate;
  private final Context context;
  private final Scope scope;

  public TracingCommittableOffsetMetadata(
      ConsumerMessage.CommittableOffsetMetadata delegate, Context context, Scope scope) {
    this.delegate = delegate;
    this.context = context;
    this.scope = scope;
  }

  @Override
  public Future<Done> commitScaladsl() {
    return delegate.commitScaladsl();
  }

  @Override
  public CompletionStage<Done> commitJavadsl() {
    return delegate.commitJavadsl();
  }

  @Override
  public Future<Done> commitInternal() {
    return delegate.commitInternal();
  }

  @Override
  public long batchSize() {
    return delegate.batchSize();
  }

  @Override
  public ConsumerMessage.PartitionOffset partitionOffset() {
    return delegate.partitionOffset();
  }

  @Override
  public String metadata() {
    return delegate.metadata();
  }

  public Context getContext() {
    return context;
  }

  public Scope getScope() {
    return scope;
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
