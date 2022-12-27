package cz.smarteon.loxone.system.status;

/**
 * Miniserver extension which is not recognized by this library.
 */
public class UnrecognizedExtension extends Extension {

    UnrecognizedExtension(final Extension e) {
        super(e.type, e.code, e.name, e.serialNumber, e.version, e.hwVersion, e.online, e.dummy, e.occupied, e.interfered,
                e.intDev, e.updating, e.updateProgress);
    }
}
