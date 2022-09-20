package cz.smarteon.loxone.system.status;

/**
 * Represents miniserver extension status having only basic set of attributes.
 */
public class BasicExtension extends Extension {

    BasicExtension(final Extension e) {
        super(e.type, e.code, e.name, e.serialNumber, e.version, e.hwVersion, e.online, e.dummy, e.occupied, e.interfered,
                e.intDev, e.updating, e.updateProgress);
    }
}
