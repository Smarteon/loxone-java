package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

import java.io.IOException;

class LoxoneValueTypeResolver implements TypeIdResolver {

    private JavaType baseType;

    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        if (id.contains("dev/sys/getkey")
                || id.contains("dev/sys/getvisusalt")) {
            return context.constructSpecializedType(baseType, Hashing.class);
        } else if (id.contains("dev/cfg/api")) {
            return context.constructSpecializedType(baseType, ApiInfo.class);
        } else if (id.contains("dev/sys/getPublicKey")) {
            return context.constructSpecializedType(baseType, PubKeyInfo.class);
        } else {
            return context.constructSpecializedType(baseType, JsonValue.class);
        }
    }

    @Override
    public String getDescForKnownTypeIds() {
        return null;
    }

    @Override
    public String idFromValue(Object value) {
        return null;
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return null;
    }

    @Override
    public String idFromBaseType() {
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
