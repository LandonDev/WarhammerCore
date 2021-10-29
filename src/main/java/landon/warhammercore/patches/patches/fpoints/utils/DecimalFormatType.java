package landon.warhammercore.patches.patches.fpoints.utils;

import java.text.DecimalFormat;

public enum DecimalFormatType {
    MONEY(new DecimalFormat("#,###.##")),
    SECONDS(new DecimalFormat("#.#")),
    LOCATION(new DecimalFormat("#.##"));

    DecimalFormatType(DecimalFormat format) {
        this.format = format;
    }

    private DecimalFormat format;

    public DecimalFormat getFormat() {
        return this.format;
    }

    public String format(Number value) {
        return this.format.format(value);
    }
}
