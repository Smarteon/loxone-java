package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

import java.io.IOException;

import static cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_CFG_API;
import static cz.smarteon.loxone.message.LoxoneMessageCommand.DEV_SYS_GETPUBLICKEY;

class LoxoneValueTypeResolver implements TypeIdResolver {

    private JavaType baseType;

    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        if (id.contains("dev/sys/getkey")
                || id.contains("dev/sys/getvisusalt")) {
            return context.constructSpecializedType(baseType, Hashing.class);
        } else if (DEV_CFG_API.is(id)) {
            return context.constructSpecializedType(baseType, DEV_CFG_API.getValueType());
        } else if (DEV_SYS_GETPUBLICKEY.is(id)) {
            return context.constructSpecializedType(baseType, DEV_SYS_GETPUBLICKEY.getValueType());
        } else if (id.contains("dev/sps/LoxAPPversion3")) {
            return context.constructSpecializedType(baseType, DateValue.class);
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
