package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneException
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class LoxoneAppTest extends Specification implements SerializationSupport {

    private static final Date LAST_MODIFIED = parseDate('2017-11-22 18:41:01')
    private static final LoxoneUuid UUID_CONTROL = new LoxoneUuid('0f869a64-0200-0a9b-ffffd4c75dbaf53c')
    private static final LoxoneUuid UUID_ROOM = new LoxoneUuid('0f869a64-0200-0a9b-ffffd4c75dbaf53d')
    private static final LoxoneUuid UUID_ROOM2 = new LoxoneUuid('0f869a64-0200-0a9c-ffffd4c75dbaf53d')
    private static final LoxoneUuid UUID_CATEGORY = new LoxoneUuid('0f869a64-0200-0a9b-ffffd4c75dbaf53e')
    private static final LoxoneUuid UUID_CATEGORY2 = new LoxoneUuid('0f869a64-0200-0a9c-ffffd4c75dbaf53e')

    def "should deserialize"() {
        when:
        LoxoneApp config = readResource('app/LoxAPP3.json', LoxoneApp)

        then:
        config.lastModified == LAST_MODIFIED
        config.miniserverInfo?.name == 'ShowRoom'
        config.rooms.size() == 3
        config.rooms.values().first() instanceof Room
        config.categories.size() == 3
        config.categories.values().first() instanceof Category
        config.controls.size() == 6
        config.controls.values().first() instanceof AlarmControl
    }

    def "should getControl by type"() {
        given:
        LoxoneApp config = new LoxoneApp(
                LAST_MODIFIED,
                Mock(MiniserverInfo),
                [(UUID_ROOM): room],
                [(UUID_CATEGORY): category],
                [(UUID_CONTROL): alarmControl])

        expect:
        config.getControl(AlarmControl) == alarmControl

        where:
        alarmControl << [new AlarmControl()]
        room << [new Room(UUID_ROOM, "room")]
        category << [new Category(UUID_CATEGORY, "category")]
    }

    def "should getControl by name and type"() {
        given:
        def control = new SwitchControl()
        control.name = 'SomeControl'
        def room = new Room(UUID_ROOM, "SomeRoom")
        def category = new Category(UUID_CATEGORY, "SomeCategory")
        LoxoneApp config = new LoxoneApp(
                LAST_MODIFIED,
                Mock(MiniserverInfo),
                [(UUID_ROOM): room],
                [(UUID_CATEGORY): category],
                [(UUID_CONTROL): control])

        expect:
        config.getControl('SomeControl', SwitchControl) == control
    }

    def "should getControlsForRoom by room"() {
        when:
        LoxoneApp config = readResource('app/LoxAPP3.json', LoxoneApp)
        LoxoneUuid roomUuid = new LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")

        then:
        def room = config.getRooms().get(roomUuid)
        room != null
        def controls = config.getControlsForRoom(room as Room)
        controls.size() == 4
    }


    def "should getControlsForCategory by category"() {
        when:
        LoxoneApp config = readResource('app/LoxAPP3.json', LoxoneApp)
        LoxoneUuid categoryUuid = new LoxoneUuid("0f869a64-0221-0b28-ffffd4c75dbaf53c")

        then:
        def category = config.getCategories().get(categoryUuid)
        category != null
        def controls = config.getControlsForCategory(category as Category)
        controls.size() == 2
    }

    def "should getControlsForRoomAndCategory by room and category"() {
        when:
        LoxoneApp config = readResource('app/LoxAPP3.json', LoxoneApp)
        LoxoneUuid roomUuid = new LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        LoxoneUuid categoryUuid = new LoxoneUuid("0f869a64-0221-0b28-ffffd4c75dbaf53c")

        then:
        def room = config.getRooms().get(roomUuid)
        room != null
        def category = config.getCategories().get(categoryUuid)
        category != null
        def controls = config.getControlsForRoomAndCategory(room as Room, category as Category)
        controls.size() == 2
    }

    def "should getRoomByName by room name"() {
        setup:
        def control = new SwitchControl()
        control.name = 'SomeControl'
        def room = new Room(UUID_ROOM, "SomeRoom")
        def category = new Category(UUID_CATEGORY, "SomeCategory")
        LoxoneApp config = new LoxoneApp(
                LAST_MODIFIED,
                Mock(MiniserverInfo),
                [(UUID_ROOM): room],
                [(UUID_CATEGORY): category],
                [(UUID_CONTROL): control])

        when:
        config.getRoomByName("SomeRoom") == room
        config.getRoomByName("someroom") == room
        config.getRoomByName("sOMErOOM") == room
        config.getRoomByName("SomeOtherRoom")

        then:
        LoxoneException ex = thrown()
        ex.message.contains("No room found with name SomeOtherRoom")
    }

    def "should get exception getRoomByName by room name with duplicate room name"() {
        setup:
        def control = new SwitchControl()
        control.name = 'SomeControl'
        def room = new Room(UUID_ROOM, "SomeRoom")
        def room2 = new Room(UUID_ROOM2, "SomeRoom")
        def category = new Category(UUID_CATEGORY, "SomeCategory")
        LoxoneApp config = new LoxoneApp(
                LAST_MODIFIED,
                Mock(MiniserverInfo),
                [(UUID_ROOM): room, (UUID_ROOM2): room2],
                [(UUID_CATEGORY): category],
                [(UUID_CONTROL): control])

        when:
        config.getRoomByName("SomeRoom")

        then:
        LoxoneException ex = thrown()
        ex.message.contains("Multiple rooms found <2> for name SomeRoom")
    }

    def "should getCategoryByName by category name"() {
        setup:
        def control = new SwitchControl()
        control.name = 'SomeControl'
        def room = new Room(UUID_ROOM, "SomeRoom")
        def category = new Category(UUID_CATEGORY, "SomeCategory")
        LoxoneApp config = new LoxoneApp(
                LAST_MODIFIED,
                Mock(MiniserverInfo),
                [(UUID_ROOM): room],
                [(UUID_CATEGORY): category],
                [(UUID_CONTROL): control])

        when:
        config.getCategoryByName("SomeCategory") == category
        config.getCategoryByName("somecategory") == category
        config.getCategoryByName("sOMEcATEGORY") == category
        config.getCategoryByName("SomeOtherCategory") == category

        then:
        LoxoneException ex = thrown()
        ex.message.contains("No category found with name SomeOtherCategory")
    }

    def "should get exception getCategoryByName by category name with duplicate category name"() {
        setup:
        def control = new SwitchControl()
        control.name = 'SomeControl'
        def room = new Room(UUID_ROOM, "SomeRoom")
        def category = new Category(UUID_CATEGORY, "SomeCategory")
        def category2 = new Category(UUID_CATEGORY2, "SomeCategory")
        LoxoneApp config = new LoxoneApp(
                LAST_MODIFIED,
                Mock(MiniserverInfo),
                [(UUID_ROOM): room],
                [(UUID_CATEGORY): category, (UUID_CATEGORY2): category2],
                [(UUID_CONTROL): control])

        when:
        config.getCategoryByName("SomeCategory")

        then:
        LoxoneException ex = thrown()
        ex.message.contains("Multiple categories found <2> for name SomeCategory")
    }
}
