package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.XmlAttribute;

public abstract class UpdatableDevice extends Device implements Updatable {

    @XmlAttribute(name = "Updating") protected Boolean updating;
    @XmlAttribute(name = "UpdateProgress") protected Integer updateProgress;

    UpdatableDevice() {}

    protected UpdatableDevice(final String code, final String name, final String serialNumber,
                              final Boolean updating, final Integer updateProgress) {
        super(code, name, serialNumber);
        this.updating = updating;
        this.updateProgress = updateProgress;
    }

    @Override
    public boolean isUpdating() {
        return Boolean.TRUE.equals(updating);
    }

    @Override
    public Integer getUpdateProgress() {
        return updateProgress;
    }
}
