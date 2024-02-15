package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.LoxoneUuid
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CalendarCommandTest {

    @Test
    fun `should create get command`() {
        expectThat(CalendarCommand.getEntries().command).isEqualTo("jdev/sps/calendargetentries/")
    }

    @Test
    fun `should create delete command`() {
        expectThat(CalendarCommand.deleteEntry(LoxoneUuid("00000000-0000-0000-0000000000000000")).command)
            .isEqualTo("jdev/sps/calendardeleteentry/00000000-0000-0000-0000000000000000")
    }
}
