package org.scenario.util;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

public final class Output {
    public final static ColoredPrinter warn = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.YELLOW)
            .attribute(Ansi.Attribute.BOLD)
            .build();

    public final static ColoredPrinter error = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.RED)
            .attribute(Ansi.Attribute.BOLD)
            .build();

    public final static ColoredPrinter success = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.GREEN)
            .attribute(Ansi.Attribute.BOLD)
            .build();

    public final static ColoredPrinter info = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.CYAN)
            .attribute(Ansi.Attribute.BOLD)
            .build();
}
