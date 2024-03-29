package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

final class ExtensionAdapter extends XmlAdapter<Extension, Extension> {

    @Override
    @SuppressWarnings({"checkstyle:returncount", "checkstyle:cyclomaticcomplexity"})
    public Extension unmarshal(final Extension v) {
        final String type = v.getType();
        if (type == null) {
            return new UnrecognizedExtension(v);
        }
        switch (type) {
            case "Extension":
            case "Relay Extension":
            case "RS485 Extension":
            case "DMX Extension":
            case "DI Extension":
            case "Modbus Extension":
            case "Dimmer Extension":
            case "AO Extension":
            case "AI Extension":
            case "RS232 Extension":
            case "KNX Extension":
                return new BasicExtension(v);
            case "Dali Extension":
                return new DaliExtension(v);
            case "Tree Extension":
                return new TreeExtension(v);
            case "Air Base Extension":
                return new AirBaseExtension(v);
            case "1-Wire Extension":
                return new OneWireExtension(v);
            default:
                return new UnrecognizedExtension(v);
        }
    }

    @Override
    public Extension marshal(final Extension v) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
