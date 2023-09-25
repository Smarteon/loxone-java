package cz.smarteon.loxone.calendar

import cz.smarteon.loxone.Loxone
import cz.smarteon.loxone.LoxoneEndpoint
import org.junit.jupiter.api.Test

class CalGetEventsTest {

    private val loxone: Loxone

    init {
        val loxEndpoint = LoxoneEndpoint("")
        loxone = Loxone(loxEndpoint, "", "")
    }

    @Test
    fun `should control display file`() {
        val events = loxone.http().get(CalendarCommand.getEntries(), loxone.auth())
        println(events.value)
    }
}
