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


package com.io7m.gatwick.iovar;

import com.io7m.gatwick.device.api.GWDeviceCommandRequestData;
import com.io7m.gatwick.device.api.GWDeviceCommandSetData;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.jattribute.core.AttributeReadableType;
import com.io7m.jattribute.core.AttributeReceiverType;
import com.io7m.jattribute.core.AttributeSubscriptionType;
import com.io7m.jattribute.core.AttributeType;
import com.io7m.jattribute.core.Attributes;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;

/**
 * The basic I/O variable implementation.
 *
 * @param <T> The type of values
 */

public final class GWIOVariable<T> implements GWIOVariableType<T>
{
  private final AttributeType<T> attribute;
  private final byte[] bufferData;
  private final ByteBuffer buffer;
  private final GWDeviceType device;
  private final GWIOVariableSerializeType<T> serializer;
  private final GWIOVariableDeserializeType<T> deserializer;
  private final GWDeviceCommandSetData cmdWrite;
  private final GWDeviceCommandRequestData cmdRead;
  private final int address;
  private final GWIOVariableInformation<T> info;

  private GWIOVariable(
    final GWDeviceType inDevice,
    final Attributes inAttributes,
    final GWIOVariableSerializeType<T> inSerializer,
    final GWIOVariableDeserializeType<T> inDeserializer,
    final int inSize,
    final GWIOVariableInformation<T> inInfo,
    final int inAddress)
  {
    this.device =
      Objects.requireNonNull(inDevice, "inDevice");
    this.serializer =
      Objects.requireNonNull(inSerializer, "inSerializer");
    this.deserializer =
      Objects.requireNonNull(inDeserializer, "inDeserializer");
    this.attribute =
      Objects.requireNonNull(inAttributes, "inAttributes")
        .fromFunction(inInfo::valueInitial);
    this.info =
      Objects.requireNonNull(inInfo, "inInfo");

    this.address = inAddress;
    this.bufferData = new byte[inSize];
    this.buffer = ByteBuffer.wrap(this.bufferData);

    this.cmdWrite =
      new GWDeviceCommandSetData(this.address(), this.bufferData);
    this.cmdRead =
      new GWDeviceCommandRequestData(this.address, this.bufferData.length);
  }

  /**
   * Create a new I/O variable.
   *
   * @param inDevice       The underlying device
   * @param inAttributes   An attribute source
   * @param inSerializer   A value serializer
   * @param inDeserializer A value deserializer
   * @param inSize         The serialized size of values
   * @param inInfo         The variable information
   * @param inAddress      The variable address
   * @param <T>            The type of value
   *
   * @return A new variable
   */

  public static <T> GWIOVariableType<T> create(
    final GWDeviceType inDevice,
    final Attributes inAttributes,
    final GWIOVariableSerializeType<T> inSerializer,
    final GWIOVariableDeserializeType<T> inDeserializer,
    final int inSize,
    final GWIOVariableInformation<T> inInfo,
    final int inAddress)
  {
    return new GWIOVariable<>(
      inDevice,
      inAttributes,
      inSerializer,
      inDeserializer,
      inSize,
      inInfo,
      inAddress
    );
  }

  @Override
  public int address()
  {
    return this.address;
  }

  @Override
  public void set(final T x)
    throws InterruptedException, GWDeviceException
  {
    this.buffer.rewind();
    this.serializer.serializeTo(this.buffer, x);

    this.device.sendCommand(this.cmdWrite);
    this.attribute.set(x);
  }

  @Override
  public void readFromDevice()
    throws InterruptedException, GWDeviceException
  {
    final var response =
      this.device.sendCommand(this.cmdRead);

    this.buffer.rewind();
    this.buffer.put(0, response.data());

    final var newValue = this.deserializer.deserializeFrom(this.buffer);
    this.attribute.set(newValue);
  }

  @Override
  public T get()
  {
    return this.attribute.get();
  }

  @Override
  public <B> AttributeReadableType<B> mapR(
    final Function<T, B> f)
  {
    return this.attribute.mapR(f);
  }

  @Override
  public AttributeSubscriptionType subscribe(
    final AttributeReceiverType<T> receiver)
  {
    return this.attribute.subscribe(receiver);
  }

  @Override
  public GWIOVariableInformation<T> information()
  {
    return this.info;
  }
}
