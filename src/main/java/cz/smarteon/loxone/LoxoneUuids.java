package cz.smarteon.loxone;

import java.util.ArrayList;

public class LoxoneUuids extends ArrayList<LoxoneUuid> {

    public LoxoneUuid only() {
        if (hasOnlyOne()) {
            return get(0);
        } else {
            throw new IllegalStateException("There are more uuids, not the only one");
        }
    }

    public boolean hasOnlyOne() {
        return size() == 1;
    }

}
