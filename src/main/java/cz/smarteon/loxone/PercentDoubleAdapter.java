package cz.smarteon.loxone;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Used to correctly un/marshall percent to double.
 */
public class PercentDoubleAdapter extends XmlAdapter<String, Double> {
    @Override @SuppressWarnings("checkstyle:returncount")
    public Double unmarshal(final String stringVal) {
        if (stringVal == null) {
            return null;
        } else if (stringVal.isEmpty()) {
            return -1.0;
        } else {
            return Double.valueOf(stripPercent(stringVal));
        }
    }

    @Override
    public String marshal(final Double v) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static String stripPercent(final String toStrip) {
        if (toStrip != null && toStrip.endsWith("%")) {
            return toStrip.substring(0, toStrip.length() - 1);
        } else {
            throw new IllegalArgumentException("Invalid percentage value: " + toStrip);
        }
    }
}
