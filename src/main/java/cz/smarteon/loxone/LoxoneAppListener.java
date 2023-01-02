package cz.smarteon.loxone;

import cz.smarteon.loxone.app.LoxoneApp;
import org.jetbrains.annotations.NotNull;

/**
 * Loxone app listener.
 */
@FunctionalInterface
public interface LoxoneAppListener {

    void onLoxoneApp(@NotNull LoxoneApp loxoneApp);
}
