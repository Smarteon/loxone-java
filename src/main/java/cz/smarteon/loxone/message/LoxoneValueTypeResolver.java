package cz.smarteon.loxone.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import cz.smarteon.loxone.calendar.CalEntryListValue;
import cz.smarteon.loxone.user.UserCommand;

import java.util.regex.Pattern;

import static cz.smarteon.loxone.message.LoxoneMessageCommand.COMMANDS;

class LoxoneValueTypeResolver implements TypeIdResolver {

    private static final Pattern devStatisticsPattern = Pattern.compile("dev/sys/wsdevice/[^/]+/Statistics");
    private JavaType baseType;

    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    @Override
    @SuppressWarnings("checkstyle:returncount")
    public JavaType typeFromId(DatabindContext context, String id) {
        if (id.contains("dev/sys/getkey")
                || id.contains("dev/sys/getvisusalt")) {
            return context.constructSpecializedType(baseType, Hashing.class);
        } else if (id.contains("token")) {
            return context.constructSpecializedType(baseType, Token.class);
        } else if (id.contains("dev/sys/ExtStatistics") || devStatisticsPattern.matcher(id).matches()) {
            return context.constructSpecializedType(baseType, OneWireDetails.class);
        } else if (id.contains("dev/sps") && (id.contains("user") || id.contains("getgrouplist"))) {
            return context.constructSpecializedType(baseType, UserCommand.getUserCommandValueType(id));
        } else if (id.contains("dev/sps/calendargetentries")) {
            return context.constructSpecializedType(baseType, CalEntryListValue.class);
        } else {
            return COMMANDS.stream()
                    .filter(command -> command.is(id))
                    .findFirst()
                    .map(command -> context.constructSpecializedType(baseType, command.getValueType()))
                    .orElseGet(() -> context.constructSpecializedType(baseType, JsonValue.class));
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
