package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

final class TreeDeviceAdapter extends XmlAdapter<TreeDeviceBase, TreeDevice> {
    @Override
    public TreeDevice unmarshal(final TreeDeviceBase v) {
        if (v.airDevices != null) {
            return new TreeToAirBridge(v);
        } else {
            return new TreeDevice(v);
        }
    }

    @Override
    public TreeDeviceBase marshal(final TreeDevice v) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
