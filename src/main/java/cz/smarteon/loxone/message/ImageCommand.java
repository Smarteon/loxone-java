package cz.smarteon.loxone.message;

import cz.smarteon.loxone.Command;
import cz.smarteon.loxone.app.MiniserverType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * Represents command for loxone image item.
 */
public class ImageCommand extends Command<ByteBuffer> {


    ImageCommand(final String imageUuid,
                 final MiniserverType[] supportedMiniservers) {
        super(imageUuid,
                Type.FILE, ByteBuffer.class, true, true, supportedMiniservers);
    }

    /**
     * Creates generic control command supporting all miniservers resulting in {@link JsonValue}.
     * @param uuid image identifier
     * @return control command
     */
    @NotNull
    public static ImageCommand genericControlCommand(final @NotNull String uuid) {
        return genericControlCommand(uuid, MiniserverType.KNOWN);
    }

    /**
     * Creates generic control command resulting in {@link JsonValue}.
     * @param uuid image identifier
     * @param supportedMiniservers miniservers supporting this command
     * @return control command
     */
    @NotNull
    public static ImageCommand genericControlCommand(
            final @NotNull String uuid,
            final @NotNull MiniserverType[] supportedMiniservers) {
        return new ImageCommand(uuid, supportedMiniservers);
    }
}
