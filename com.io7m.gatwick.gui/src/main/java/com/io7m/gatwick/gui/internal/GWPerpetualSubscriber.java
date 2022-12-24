/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.gatwick.gui.internal;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

/**
 * A flow subscriber that requests messages until the flow is closed.
 *
 * @param <T> The type of messages
 */

public final class GWPerpetualSubscriber<T> implements Flow.Subscriber<T>
{
  private final Consumer<T> consumer;
  private Flow.Subscription subscription;

  /**
   * A flow subscriber that requests messages until the flow is closed.
   *
   * @param inConsumer The message consumer
   */

  public GWPerpetualSubscriber(
    final Consumer<T> inConsumer)
  {
    this.consumer =
      Objects.requireNonNull(inConsumer, "consumer");
  }

  @Override
  public void onSubscribe(
    final Flow.Subscription inSubscription)
  {
    this.subscription = inSubscription;
    this.subscription.request(1L);
  }

  @Override
  public void onNext(
    final T item)
  {
    this.consumer.accept(item);
    this.subscription.request(1L);
  }

  @Override
  public void onError(
    final Throwable throwable)
  {

  }

  @Override
  public void onComplete()
  {

  }
}
