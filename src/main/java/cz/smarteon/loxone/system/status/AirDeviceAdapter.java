package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

final class AirDeviceAdapter extends XmlAdapter<AirDeviceBase, AirDevice> {
    @Override
    public AirDevice unmarshal(final AirDeviceBase v) {
        if (v.oneWireDevices != null) {
            return new MultiExtensionAir(v);
        } else {
            return new AirDevice(v);
        }
    }

    @Override
    public AirDeviceBase marshal(final AirDevice v) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
