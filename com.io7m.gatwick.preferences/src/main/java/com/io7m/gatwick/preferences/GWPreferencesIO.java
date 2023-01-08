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


package com.io7m.gatwick.preferences;

import com.io7m.gatwick.preferences.jaxb.Device;
import com.io7m.gatwick.preferences.jaxb.Preferences;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * Functions for reading/writing preferences.
 */

public final class GWPreferencesIO
{
  private GWPreferencesIO()
  {

  }

  /**
   * Write the given preferences to the given output stream.
   *
   * @param preferences The preferences
   * @param output      The output stream
   *
   * @throws IOException On errors
   */

  public static void write(
    final GWPreferences preferences,
    final OutputStream output)
    throws IOException
  {
    Objects.requireNonNull(preferences, "preferences");
    Objects.requireNonNull(output, "output");

    final var prefs =
      packPreferences(preferences);

    try {
      final var context =
        JAXBContext.newInstance(
          "com.io7m.gatwick.preferences.jaxb");
      final var marshaller =
        context.createMarshaller();

      marshaller.marshal(prefs, output);
    } catch (final JAXBException e) {
      throw new IOException(e);
    }
  }

  private static Preferences packPreferences(
    final GWPreferences preferences)
  {
    final var out = new Preferences();
    out.setDevice(packPreferencesDevice(preferences.device()));
    return out;
  }

  private static Device packPreferencesDevice(
    final GWPreferencesDevice device)
  {
    final var dev = new Device();
    dev.setShowFakeDevices(Boolean.valueOf(device.showFakeDevices()));
    return dev;
  }

  /**
   * Parse preferences from the given input stream.
   *
   * @param source The source URI
   * @param stream The source stream
   *
   * @return Parsed preferences
   *
   * @throws IOException On errors
   */

  public static GWPreferences parse(
    final URI source,
    final InputStream stream)
    throws IOException
  {
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(stream, "stream");

    try {
      final var schemas =
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      final var schema =
        schemas.newSchema(
          GWPreferencesIO.class.getResource(
            "/com/io7m/gatwick/preferences/preferences-1.xsd")
        );

      final var context =
        JAXBContext.newInstance(
          "com.io7m.gatwick.preferences.jaxb");
      final var unmarshaller =
        context.createUnmarshaller();

      unmarshaller.setSchema(schema);

      final var streamSource =
        new StreamSource(stream, source.toString());

      return unpackPreferences(
        (Preferences) unmarshaller.unmarshal(streamSource)
      );
    } catch (final SAXException | JAXBException e) {
      throw new IOException(e);
    }
  }

  private static GWPreferences unpackPreferences(
    final Preferences prefs)
  {
    return new GWPreferences(unpackPreferencesDevice(prefs.getDevice()));
  }

  private static GWPreferencesDevice unpackPreferencesDevice(
    final Device device)
  {
    return new GWPreferencesDevice(
      Optional.ofNullable(device.isShowFakeDevices())
        .orElse(Boolean.FALSE)
        .booleanValue()
    );
  }
}
