package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * This deserializer workarounds several BUGs in jackson-dataformat-xml library making it unusable for polymorphic
 * deserialization based on properties. Namely:
 * <ul>
 *     <li>It doesn't work properly when the type property is not the first</li>
 *     <li>Unwrapped collection doesn't work with polymorphism in many cases</li>
 * </ul>
 */
final class AirDeviceDeserializer extends JsonDeserializer<AirDevice> {

    @Override
    public AirDevice deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final AirDeviceBase airDevice = ctxt.readValue(p, AirDeviceBase.class);
        if (airDevice.oneWireDevices != null) {
            return new MultiExtensionAir(airDevice);
        }
        return new AirDevice(airDevice);
    }
}
