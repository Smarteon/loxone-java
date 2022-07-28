package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneException;
import cz.smarteon.loxone.LoxoneUuid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Represents the loxone application as used in user interface.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoxoneApp implements Serializable {

    private final Date lastModified;
    private final MiniserverInfo miniserverInfo;
    private final Map<LoxoneUuid, Room> rooms;
    private final Map<LoxoneUuid, Category> categories;
    private final Map<LoxoneUuid, Control> controls;

    @JsonCreator
    public LoxoneApp(@JsonProperty("lastModified") Date lastModified,
                     @JsonProperty("msInfo") MiniserverInfo miniserverInfo,
                     @JsonProperty("rooms") Map<LoxoneUuid, Room> rooms,
                     @JsonProperty("cats") Map<LoxoneUuid, Category> categories,
                     @JsonProperty("controls") Map<LoxoneUuid, Control> controls) {
        this.lastModified = requireNonNull(lastModified, "lastModified can't be null");
        this.miniserverInfo = requireNonNull(miniserverInfo, "miniserverInfo can't be null");
        this.rooms = requireNonNull(rooms, "rooms can't be null");
        this.categories = requireNonNull(categories, "categories can't be null");
        this.controls = requireNonNull(controls, "controls can't be null");
    }

    @NotNull
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Gets this miniserver info.
     *
     * @return miniserver info as {@link MiniserverInfo}
     */
    @NotNull
    public MiniserverInfo getMiniserverInfo() {
        return miniserverInfo;
    }

    @NotNull
    public Map<LoxoneUuid, Room> getRooms() {
        return rooms;
    }

    @NotNull
    public Map<LoxoneUuid, Category> getCategories() {
        return categories;
    }

    @NotNull
    public Map<LoxoneUuid, Control> getControls() {
        return controls;
    }

    /**
     * @param type control type to get
     * @param <T> class of control type
     * @return the only control of given type, or null if no such control exists
     * @throws cz.smarteon.loxone.LoxoneException if more than one control of given type exists
     */
    @JsonIgnore
    @Nullable
    public <T extends Control> T getControl(final @NotNull Class<T> type) {
        final Collection<T> found = getControls(type);
        switch (found.size()) {
            case 0:
                return null;
            case 1:
                return found.iterator().next();
            default:
                throw new LoxoneException("More than one control of type " + type.getSimpleName() + " found!");
        }
    }

    /**
     * @param type control type to get
     * @param <T> class of control type
     * @return collection of found control for given type (may be empty)
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
    @NotNull
    public <T extends Control> Collection<T> getControls(final @NotNull Class<T> type) {
        requireNonNull(type, "control type can't be null");
        final List<T> found = new ArrayList<>();
        for (Control c : controls.values()) {
            if ( type.isAssignableFrom(c.getClass())) {
                found.add((T) c);
            }
        }
        return found;
    }

    /**
     * Get single (if any) control of given type and name
     * @param name control name
     * @param type control type
     * @param <T> class of control type
     * @return the only control of given name and type, or null if no such control exists
     * @throws cz.smarteon.loxone.LoxoneException if more than one control of given name and type exists
     */
    @JsonIgnore
    @Nullable
    public <T extends Control> T getControl(final @NotNull String name, final @NotNull Class<T> type) {
        requireNonNull(name, "control name can't be null");
        T found = null;
        for (T control : getControls(type)) {
            if (name.equals(control.getName())) {
                if (found != null) {
                    throw new LoxoneException("More than one control of name " + name + " and type " + type.getSimpleName() + " found!");
                } else {
                    found = control;
                }
            }
        }
        return found;
    }

    /**
     * Filters the controls with the room specified.
     *
     * @param room room to get the controls for
     * @return collection of found controls assigned to given room or empty list if none found
     */
    @JsonIgnore
    @NotNull
    public List<Control> getControlsForRoom(final @NotNull Room room) {
        requireNonNull(room, "room can't be null");
        requireNonNull(room.getUuid(), "room uuid can't be null");
        return getControls().values().stream()
                .filter(c -> room.getUuid().equals(c.getRoomUuid()))
                .collect(Collectors.toList());
    }

    /**
     * Filters the controls with the category specified.
     *
     * @param category category to get the controls for
     * @return collection of found controls assigned to given category or empty list if none found
     */
    @JsonIgnore
    @NotNull
    public List<Control> getControlsForCategory(final @NotNull Category category) {
        requireNonNull(category, "category can't be null");
        requireNonNull(category.getUuid(), "category uuid can't be null");
        return getControls().values().stream()
                .filter(c -> category.getUuid().equals(c.getCategoryUuid()))
                .collect(Collectors.toList());
    }

    /**
     * Filters the controls with the room and category specified.
     *
     * @param room room to get the controls for
     * @param category category to get the controls for
     * @return collection of found control assigned to given category and room or empty list if none found
     */
    @JsonIgnore
    @NotNull
    public List<Control> getControlsForRoomAndCategory(final @NotNull Room room, final @NotNull Category category) {
        requireNonNull(room, "room can't be null");
        requireNonNull(room.getUuid(), "room uuid can't be null");
        requireNonNull(category, "category can't be null");
        requireNonNull(category.getUuid(), "category uuid can't be null");
        return getControls().values().stream()
                .filter(c -> room.getUuid().equals(c.getRoomUuid()))
                .filter(c -> category.getUuid().equals(c.getCategoryUuid()))
                .collect(Collectors.toList());
    }

    /**
     * Get the room object by its name. If zero or more than one are found it throws an exception.
     *
     * @param name the name of the room
     * @return found room with given name
     * @throws LoxoneException when zero or more than one room is found for the supplied name
     */
    @JsonIgnore
    @NotNull
    public Room getRoomByName(final @NotNull String name) {
        List<Room> roomList = getRooms().values().stream()
                .filter(r -> name.equalsIgnoreCase(r.getName()))
                .collect(Collectors.toList());

        if (roomList.size() == 1) {
            return roomList.get(0);
        } else if (roomList.isEmpty()) {
            throw new LoxoneException("No room found with name " + name + "!");
        } else {
            throw new LoxoneException("Multiple rooms found <" + roomList.size() + "> for name " + name + "!");
        }
    }

    /**
     * Get the category object by its name. If zero or more than one are found it throws an exception.
     *
     * @param name the name of the category
     * @return found category with given name
     * @throws LoxoneException when zero or more than one category is found for supplied the name
     */
    public Category getCategoryByName(final @NotNull String name) {
        List<Category> categoryList = getCategories().values().stream()
                .filter(c -> name.equalsIgnoreCase(c.getName()))
                .collect(Collectors.toList());

        if (categoryList.size() == 1) {
            return categoryList.get(0);
        } else if (categoryList.isEmpty()) {
            throw new LoxoneException("No category found with name " + name + "!");
        } else {
            throw new LoxoneException("Multiple categories found <" + categoryList.size() + "> for name " + name + "!");
        }
    }
}
