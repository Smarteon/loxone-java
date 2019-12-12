package cz.smarteon.loxone.message


import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class LoxoneValueTypeResolverTest extends Specification {

    @Subject LoxoneValueTypeResolver resolver = new LoxoneValueTypeResolver()
    DeserializationContext deserializationContext

    void setup() {
        def mapper = new ObjectMapper()
        resolver.init(mapper.constructType(LoxoneValue))
        deserializationContext = new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance)
                .createInstance(mapper.deserializationConfig, mapper.getFactory().createParser(''), new InjectableValues.Std())
    }

    @Unroll
    def "should resolve #simpleType for #typeId"() {
        expect:
        resolver.typeFromId(deserializationContext, typeId).getRawClass() == type

        where:
        [typeId, type] << [
                ['jdev/sys/getkey2/someUser', Hashing],
                ['jdev/sys/getvisusalt/someUser', Hashing],
                ['jdev/sys/ExtStatistics/extSerialNum', OneWireDetails],
                ['jdev/sys/gettoken/hash/user/2/clientUuid/clientInfo', Token],
                ['jdev/sys/refreshtoken/hash/user', Token],
                ['authwithtoken/hash/user', Token],
        ] + LoxoneMessageCommand.COMMANDS.collect { cmd -> [cmd.command, cmd.valueType] }

        simpleType = type.simpleName

    }
}
