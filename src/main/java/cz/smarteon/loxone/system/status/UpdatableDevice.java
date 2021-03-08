package cz.smarteon.loxone.system.status;

public abstract class UpdatableDevice extends Device implements Updatable {

    protected final Boolean updating;
    protected final Integer updateProgress;

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
