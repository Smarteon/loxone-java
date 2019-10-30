package cz.smarteon.loxone.system.status;

import java.util.List;

/**
 * Marker interface for part of status containing collection of {@link Device}.
 * @param <D> type of devices provided
 */
public interface DevicesProvider<D extends Device> {

    /**
     * Provides list of devices, should not return null.
     * @return list of devices or empty list
     */
    List<D> getDevices();
}
