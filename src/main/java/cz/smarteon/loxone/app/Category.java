package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.smarteon.loxone.LoxoneUuid;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;

/**
 * Represents Category.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Jacksonized
@Builder
@Value
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Category {

    /**
     * UUID of this category, should be unique.
     */
    @JsonProperty(value = "uuid", required = true)
    @NotNull
    LoxoneUuid uuid;

    /**
     * Category name - usually localized, non unique.
     */
    @JsonProperty(value = "name", required = true)
    @NotNull
    String name;

    /**
     * Category image - filename of the image.
     */
    @JsonProperty(value = "image")
    String image;
}
