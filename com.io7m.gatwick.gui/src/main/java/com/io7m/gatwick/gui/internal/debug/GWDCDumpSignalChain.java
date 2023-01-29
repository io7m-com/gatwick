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


package com.io7m.gatwick.gui.internal.debug;

import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceType;
import com.io7m.gatwick.gui.internal.gt.GWGTK1LongRunning;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

final class GWDCDumpSignalChain extends GWDCAbstract
{
  GWDCDumpSignalChain(
    final RPServiceDirectoryType inServices,
    final String inName,
    final List<String> inArguments)
  {
    super(inServices, inName, inArguments);
  }

  @Override
  public void execute(
    final BufferedWriter writer)
    throws Exception
  {
    final var args = this.arguments();
    if (args.size() < 1) {
      writer.write("error: usage: %s output.txt".formatted(this.name()));
      writer.newLine();
      writer.flush();
      return;
    }

    final var output =
      args.get(0);
    final var gt =
      this.services().requireService(GWGT1KServiceType.class);

    gt.executeOnDevice(GWGTK1LongRunning.TASK_SHORT, controller -> {
      final var chain =
        controller.patchCurrent()
          .chain()
          .get();

      Files.writeString(
        Paths.get(output),
        chain.elements()
          .stream()
          .map(Enum::toString)
          .collect(Collectors.joining(",\n")),
        TRUNCATE_EXISTING,
        CREATE
      );

      writer.write("info: wrote to %s".formatted(output));
      writer.newLine();
      writer.flush();
    });
  }
}
