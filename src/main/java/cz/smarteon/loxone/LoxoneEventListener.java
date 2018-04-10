package cz.smarteon.loxone;

import cz.smarteon.loxone.message.TextEvent;
import cz.smarteon.loxone.message.ValueEvent;

public abstract class LoxoneEventListener {

    public void onEvent(final ValueEvent event) {}

    public void onEvent(final TextEvent event) {}
}
