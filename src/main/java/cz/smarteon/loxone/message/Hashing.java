package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class Hashing implements LoxoneValue {
    private final byte[] key;
    private final String salt;
    private final String hashAlg;

    @JsonCreator
    public Hashing(@JsonProperty("key") @JsonDeserialize(using = HexDeserializer.class) final byte[] key,
            @JsonProperty("salt") final String salt, @JsonProperty("hashAlg") final String hashAlg) {
        this.key = key;
        this.salt = salt;
        this.hashAlg = hashAlg;
    }

    @JsonSerialize(using = HexSerializer.class)
    public byte[] getKey() {
        return key;
    }

    public String getSalt() {
        return salt;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    public String getHashAlg() {return hashAlg;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hashing hashing = (Hashing) o;

        if (!Arrays.equals(key, hashing.key)) return false;
        if (!Objects.equals(hashAlg, hashing.hashAlg)) return false;
        return Objects.equals(salt, hashing.salt);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(key);
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        result = 31 * result + (hashAlg != null ? hashAlg.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Hashing{" +
                "key=" + Arrays.toString(key) +
                ", salt='" + salt + '\'' +
                ", hashAlg='" + hashAlg + '\'' +
                '}';
    }
}
