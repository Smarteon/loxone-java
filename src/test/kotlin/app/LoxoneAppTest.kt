package cz.smarteon.loxone.app

import cz.smarteon.loxone.LoxoneException
import cz.smarteon.loxone.LoxoneUuid
import cz.smarteon.loxone.parseDate
import cz.smarteon.loxone.readResource
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.message

class LoxoneAppTest {

    @Test
    fun `should deserialize`() {
        expectThat(readResource<LoxoneApp>("app/LoxAPP3.json")) {
            get { lastModified }.isEqualTo(LAST_MODIFIED)
            get { miniserverInfo.name }.isEqualTo("ShowRoom")
            get { rooms }.hasSize(3)
                .get { values.first() }.isNotNull()
            get { categories }.hasSize(3)
                .get { values.first() }.isNotNull()
            get { controls }.hasSize(6)
                .get { values.first() }.isNotNull()
        }
    }

    @Test
    fun `should getControl by type`() {
        val alarmControl = AlarmControl()
        val room = Room(UUID_ROOM, "room")
        val category = Category(UUID_CATEGORY, "category")

        expectThat(
            LoxoneApp(
                LAST_MODIFIED,
                mockk(),
                mapOf(UUID_ROOM to room),
                mapOf(UUID_CATEGORY to category),
                mapOf(UUID_CONTROL to alarmControl)
            )
        ) {
            get { getControl(AlarmControl::class.java) }.isEqualTo(alarmControl)
        }
    }

    @Test
    fun `should getControl by name and type`() {
        val switchControl = SwitchControl()
        switchControl.name = "SomeControl"
        val room = Room(UUID_ROOM, "room")
        val category = Category(UUID_CATEGORY, "category")

        expectThat(
            LoxoneApp(
                LAST_MODIFIED,
                mockk(),
                mapOf(UUID_ROOM to room),
                mapOf(UUID_CATEGORY to category),
                mapOf(UUID_CONTROL to switchControl)
            )
        ) {
            get { getControl("SomeControl", SwitchControl::class.java) }.isEqualTo(switchControl)
        }
    }

    @Test
    fun `should getControlsForRoom by room`() {
        val config = readResource<LoxoneApp>("app/LoxAPP3.json")
        val roomUuid = LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        val room = checkNotNull(config.rooms[roomUuid])

        expectThat(config) {
            get { getControlsForRoom(room) }.hasSize(4)
        }
    }

    @Test
    fun `should getControlsForCategory by category`() {
        val config = readResource<LoxoneApp>("app/LoxAPP3.json")
        val categoryUuid = LoxoneUuid("0f869a64-0221-0b28-ffffd4c75dbaf53c")
        val category = checkNotNull(config.categories[categoryUuid])

        expectThat(config) {
            get { getControlsForCategory(category) }.hasSize(2)
        }
    }

    @Test
    fun `should getControlsForRoomAndCategory by room and category`() {
        val config = readResource<LoxoneApp>("app/LoxAPP3.json")
        val categoryUuid = LoxoneUuid("0f869a64-0221-0b28-ffffd4c75dbaf53c")
        val roomUuid = LoxoneUuid("0f869a64-028d-0cc2-ffffd4c75dbaf53c")
        val category = checkNotNull(config.categories[categoryUuid])
        val room = checkNotNull(config.rooms[roomUuid])

        expectThat(config) {
            get { getControlsForRoomAndCategory(room, category) }.hasSize(2)
        }
    }

    @Test
    fun `should getRoomByName by room name`() {
        val switchControl = SwitchControl()
        switchControl.name = "SomeControl"
        val room = Room(UUID_ROOM, "SomeRoom")
        val category = Category(UUID_CATEGORY, "SomeCategory")
        val loxoneApp = LoxoneApp(
            LAST_MODIFIED,
            mockk(),
            mapOf(UUID_ROOM to room),
            mapOf(UUID_CATEGORY to category),
            mapOf(UUID_CONTROL to switchControl)
        )

        expectThat(loxoneApp) {
            get { getRoomByName("SomeRoom") }.isEqualTo(room)
            get { getRoomByName("someroom") }.isEqualTo(room)
            get { getRoomByName("sOMErOOM") }.isEqualTo(room)
        }

        expectThrows<LoxoneException> {
            loxoneApp.getRoomByName("SomeOtherRoom")
        }.message.isEqualTo("No room found with name SomeOtherRoom!")
    }

    @Test
    fun `should get exception getRoomByName by room name with duplicate room name`() {
        val switchControl = SwitchControl()
        switchControl.name = "SomeControl"
        val room = Room(UUID_ROOM, "SomeRoom")
        val room2 = Room(UUID_ROOM2, "SomeRoom")
        val category = Category(UUID_CATEGORY, "SomeCategory")
        val loxoneApp = LoxoneApp(
            LAST_MODIFIED,
            mockk(),
            mapOf(UUID_ROOM to room, UUID_ROOM2 to room2),
            mapOf(UUID_CATEGORY to category),
            mapOf(UUID_CONTROL to switchControl)
        )

        expectThrows<LoxoneException> {
            loxoneApp.getRoomByName("SomeRoom")
        }.message.isEqualTo("Multiple rooms found <2> for name SomeRoom!")
    }

    @Test
    fun `should getCategoryByName by category name`() {
        val switchControl = SwitchControl()
        switchControl.name = "SomeControl"
        val room = Room(UUID_ROOM, "SomeRoom")
        val category = Category(UUID_CATEGORY, "SomeCategory")
        val loxoneApp = LoxoneApp(
            LAST_MODIFIED,
            mockk(),
            mapOf(UUID_ROOM to room),
            mapOf(UUID_CATEGORY to category),
            mapOf(UUID_CONTROL to switchControl)
        )

        expectThat(loxoneApp) {
            get { getCategoryByName("SomeCategory") }.isEqualTo(category)
            get { getCategoryByName("somecategory") }.isEqualTo(category)
            get { getCategoryByName("sOMEcATEGORY") }.isEqualTo(category)
        }

        expectThrows<LoxoneException> {
            loxoneApp.getCategoryByName("SomeOtherCategory")
        }.message.isEqualTo("No category found with name SomeOtherCategory!")
    }

    @Test
    fun `should get exception getCategoryByName by category name with duplicate category name`() {
        val switchControl = SwitchControl()
        switchControl.name = "SomeControl"
        val room = Room(UUID_ROOM, "SomeRoom")
        val category = Category(UUID_CATEGORY, "SomeCategory")
        val category2 = Category(UUID_CATEGORY2, "SomeCategory")
        val loxoneApp = LoxoneApp(
            LAST_MODIFIED,
            mockk(),
            mapOf(UUID_ROOM to room),
            mapOf(UUID_CATEGORY to category, UUID_CATEGORY2 to category2),
            mapOf(UUID_CONTROL to switchControl)
        )

        expectThrows<LoxoneException> {
            loxoneApp.getCategoryByName("SomeCategory")
        }.message.isEqualTo("Multiple categories found <2> for name SomeCategory!")
    }

    companion object {
        private val LAST_MODIFIED = parseDate("2017-11-22 18:41:01")
        private val UUID_CONTROL = LoxoneUuid("0f869a64-0200-0a9b-ffffd4c75dbaf53c")
        private val UUID_ROOM = LoxoneUuid("0f869a64-0200-0a9b-ffffd4c75dbaf53d")
        private val UUID_ROOM2 = LoxoneUuid("0f869a64-0200-0a9c-ffffd4c75dbaf53d")
        private val UUID_CATEGORY = LoxoneUuid("0f869a64-0200-0a9b-ffffd4c75dbaf53e")
        private val UUID_CATEGORY2 = LoxoneUuid("0f869a64-0200-0a9c-ffffd4c75dbaf53e")
    }
}
