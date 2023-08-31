package cz.smarteon.loxone.calendar

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CalendarCommandTest {

    @Test
    fun `should create command`() {
        expectThat(CalendarCommand.getEntries().command).isEqualTo("jdev/sps/calendargetentries/")
    }
}
