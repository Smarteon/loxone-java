package cz.smarteon.loxone.system.status;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Represents a multi extension air extension.
 */
public class MultiExtensionAir extends AirDevice implements DevicesProvider<OneWireDevice> {

    MultiExtensionAir(final AirDeviceBase ad) {
        super(ad);
    }

    @Override
    @NotNull
    public List<OneWireDevice> getDevices() {
        return oneWireDevices != null ? oneWireDevices : Collections.emptyList();
    }

    /**
     * Serial number transformed to the form for getting one wire details.
     * @return serial number for one wire details command.
     * @see cz.smarteon.loxone.message.LoxoneMessageCommand#oneWireDetails(String)
     */
    @NotNull
    public String getSerialForOneWireDetails() {
        if (getSerialNumber() != null) {
            return getSerialNumber().replaceAll(":", "").substring(8).toLowerCase();
        } else {
            throw new IllegalStateException("MultiExtensionAir must have set serial number");
        }
    }
}
