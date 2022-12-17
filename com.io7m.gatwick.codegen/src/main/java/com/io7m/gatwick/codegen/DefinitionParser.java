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


package com.io7m.gatwick.codegen;

import com.io7m.gatwick.codegen.jaxb.Definitions;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * A parser for definitions.
 */

public final class DefinitionParser
{
  private static final Logger LOG =
    LoggerFactory.getLogger(DefinitionParser.class);

  private DefinitionParser()
  {

  }

  /**
   * Parse the included definitions.
   *
   * @return The definitions
   *
   * @throws IOException        On errors
   * @throws URISyntaxException On errors
   */

  public static Definitions parse()
    throws IOException, URISyntaxException
  {
    final var source =
      DefinitionParser.class.getResource(
        "/com/io7m/gatwick/codegen/Main.xml");

    return parse(source.toURI(), source.openStream());
  }

  /**
   * Parse the included definitions.
   *
   * @param source The source URI
   * @param stream The source stream
   *
   * @return The definitions
   *
   * @throws IOException On errors
   */

  public static Definitions parse(
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
          DefinitionParser.class.getResource(
            "/com/io7m/gatwick/codegen/gt1000.xsd")
        );

      final var context =
        JAXBContext.newInstance(
          "com.io7m.gatwick.codegen.jaxb");

      final var unmarshaller =
        context.createUnmarshaller();

      unmarshaller.setSchema(schema);

      final var parsers =
        SAXParserFactory.newInstance();
      final var parser =
        parsers.newSAXParser();
      final var reader =
        parser.getXMLReader();

      /*
       * Turn on "secure processing". Sets various resource limits to prevent
       * various denial of service attacks.
       */

      reader.setFeature(
        XMLConstants.FEATURE_SECURE_PROCESSING,
        true);

      /*
       * Don't allow access to schemas or DTD files.
       */

      reader.setProperty(
        XMLConstants.ACCESS_EXTERNAL_SCHEMA,
        "");
      reader.setProperty(
        XMLConstants.ACCESS_EXTERNAL_DTD,
        "");

      /*
       * Don't load DTDs at all.
       */

      reader.setFeature(
        "http://apache.org/xml/features/nonvalidating/load-external-dtd",
        false);

      /*
       * Enable XInclude.
       */

      reader.setFeature(
        "http://apache.org/xml/features/xinclude",
        true);

      /*
       * Ensure namespace processing is enabled.
       */

      reader.setFeature(
        "http://xml.org/sax/features/namespaces",
        true);

      /*
       * Disable validation.
       */

      reader.setFeature(
        "http://xml.org/sax/features/validation",
        false);
      reader.setFeature(
        "http://apache.org/xml/features/validation/schema",
        false);

      /*
       * Tell the parser to use the full EntityResolver2 interface (by default,
       * the extra EntityResolver2 methods will not be called - only those of
       * the original EntityResolver interface would be called).
       */

      reader.setFeature(
        "http://xml.org/sax/features/use-entity-resolver2",
        true);

      reader.setEntityResolver(new ResourceResolver());
      final var saxSource = new SAXSource(reader, new InputSource(stream));
      return (Definitions) unmarshaller.unmarshal(saxSource);
    } catch (final SAXException
                   | JAXBException
                   | ParserConfigurationException e) {
      throw new IOException(e);
    }
  }

  private static final class ResourceResolver implements EntityResolver2
  {
    ResourceResolver()
    {

    }

    @Override
    public InputSource getExternalSubset(
      final String name,
      final String baseURI)
    {
      LOG.trace("getExternalSubset: {} {}", name, baseURI);
      return null;
    }

    @Override
    public InputSource resolveEntity(
      final String name,
      final String publicId,
      final String baseURI,
      final String systemId)
      throws IOException
    {
      LOG.trace(
        "resolveEntity: {} {} {} {}",
        name,
        publicId,
        baseURI,
        systemId);

      final var url =
        DefinitionParser.class.getResource(
          "/com/io7m/gatwick/codegen/%s".formatted(systemId)
        );

      return new InputSource(url.openStream());
    }

    @Override
    public InputSource resolveEntity(
      final String publicId,
      final String systemId)
    {
      LOG.trace("resolveEntity: {} {}", publicId, systemId);
      return null;
    }
  }
}
