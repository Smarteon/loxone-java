package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Arrays;

public class Hashing implements LoxoneValue {
    private final byte[] key;
    private final String salt;

    @JsonCreator
    public Hashing(@JsonProperty("key") @JsonDeserialize(using = HexDeserializer.class) final byte[] key,
            @JsonProperty("salt") final String salt) {
        this.key = key;
        this.salt = salt;
    }

    @JsonSerialize(using = HexSerializer.class)
    public byte[] getKey() {
        return key;
    }

    public String getSalt() {
        return salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hashing hashing = (Hashing) o;

        if (!Arrays.equals(key, hashing.key)) return false;
        return salt != null ? salt.equals(hashing.salt) : hashing.salt == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(key);
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Hashing{" +
                "key=" + Arrays.toString(key) +
                ", salt='" + salt + '\'' +
                '}';
    }
}
