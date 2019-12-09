package cz.smarteon.loxone;

import cz.smarteon.loxone.app.LoxoneApp;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LoxoneAppListener {

    void onLoxoneApp(final @NotNull LoxoneApp loxoneApp);
}
