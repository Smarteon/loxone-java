package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TemperatureUnit {

    CELSIUS(0),
    FAHRENHEIT(1);

    private final int value;

    TemperatureUnit(int value) {
        this.value = value;
    }

    @JsonCreator
    public static TemperatureUnit fromValue(final int value) {
        for (TemperatureUnit temperatureUnit : values()) {
            if (temperatureUnit.value == value)
                return temperatureUnit;
        }

        throw new IllegalArgumentException("Invalid temperature unit specification, value=" + value);
    }
}
