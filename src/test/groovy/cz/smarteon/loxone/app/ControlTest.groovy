package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification
import spock.lang.Unroll


class ControlTest extends Specification implements SerializationSupport {

    @Unroll
    def "should deserialize #typeName"() {
        when:
        def control = readResource("app/${typeName.uncapitalize()}.json", Control)
        def specificStateVal = specificState != null ? control."state$specificState"() : null

        then:
        control.name == controlName
        control.secured == secured
        control.states.size() == statesSize
        specificStateVal  == expectedSpecificStateVal

        where:
        type            | controlName | statesSize | specificState | expectedSpecificStateVal
        AlarmControl    | 'Alarm'     | 10         | 'Armed'       | new LoxoneUuid('0f86a2fe-0378-3e08-ffffb2d4efc8b5b6')
        SwitchControl   | 'CallState' | 1          | 'Active'      | new LoxoneUuid('110cb849-0125-20f9-ffffac0ced78bcf2')
        PresenceControl | 'PresTest'  | 0          | null          | null

        typeName = type.simpleName
        secured = type == AlarmControl
    }
}
