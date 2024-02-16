package cz.smarteon.loxone.message

import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext
import cz.smarteon.loxone.calendar.CalEntryListValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LoxoneValueTypeResolverTest {

    private val resolver = LoxoneValueTypeResolver()
    private lateinit var deserializationContext: DefaultDeserializationContext

    @BeforeEach
    fun setup() {
        val mapper = ObjectMapper()
        resolver.init(mapper.constructType(LoxoneValue::class.java))
        deserializationContext = DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance)
            .createInstance(mapper.deserializationConfig, mapper.factory.createParser(""), InjectableValues.Std())
    }

    enum class TestClass(
        val typeId: String,
        val type: Class<*>
    ) {
        Hashing1("jdev/sys/getkey2/someUser", Hashing::class.java),
        Hashing2("jdev/sys/getvisusalt/someUser", Hashing::class.java),
        OneWireDetails1("jdev/sys/ExtStatistics/extSerialNum", OneWireDetails::class.java),
        OneWireDetails2("dev/sys/wsdevice/504F94FFFE8A7097/Statistics", OneWireDetails::class.java),
        Token1("jdev/sys/gettoken/hash/user/2/clientUuid/clientInfo", Token::class.java),
        Token2("jdev/sys/refreshtoken/hash/user", Token::class.java),
        Token3("authwithtoken/hash/user", Token::class.java),
        Calendar("dev/sps/calendargetentries", CalEntryListValue::class.java)
    }

    @ParameterizedTest
    @EnumSource(TestClass::class)
    fun `should resolve`(testParameters: TestClass) {
        expectThat(resolver.typeFromId(deserializationContext, testParameters.typeId).rawClass).isEqualTo(testParameters.type)
    }
}
