package cz.smarteon.loxone

import cz.smarteon.loxone.app.MiniserverType
import cz.smarteon.loxone.message.LoxoneMessageCommand
import net.jadler.Jadler
import net.jadler.Jadler.closeJadler
import net.jadler.Jadler.initJadlerUsing
import net.jadler.Jadler.onRequest
import net.jadler.stubbing.server.jdk.JdkStubHttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.net.URL
import kotlin.text.get
import kotlin.toString

class LoxoneHttpTest {
    private lateinit var loxoneHttp: LoxoneHttp

    @BeforeEach
    fun setup() {
        initJadlerUsing(JdkStubHttpServer())
        loxoneHttp = LoxoneHttp(LoxoneEndpoint("localhost", Jadler.port()))
    }

    @AfterEach
    fun cleanup() {
        closeJadler()
    }

    @Test
    fun `should get cfg`() {
        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo("/jdev/sys/numtasks")
            .respond()
            .withBody("{\"LL\":{\"control\": \"dev/sys/numtasks\", \"value\": \"1\", \"code\": 200}}")

        expectThat(loxoneHttp.get(LoxoneMessageCommand.DEV_SYS_NUMTASKS)).get { value.value }.isEqualTo(1)
    }

    @Test
    fun `should redirect`() {
        val finalLocation = "http://localhost:${Jadler.port()}/test2"

        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo("/test")
            .respond()
            .withStatus(302)
            .withHeader("Location", finalLocation)
        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo("/test2")
            .respond()
            .withStatus(200)
            .withBody("\"testString\"")

        expectThat(
            loxoneHttp.get(
                Command(
                    "/test",
                    Command.Type.JSON,
                    String::class.java,
                    true,
                    false,
                    MiniserverType.KNOWN
                )
            )
        ) {
            isEqualTo("testString")
            get { loxoneHttp.lastUrl }.isEqualTo(URL(finalLocation))
        }
    }

    @Test
    fun `should encode whitespace`() {
        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo("/test%20whitespace")
            .respond()
            .withStatus(200)
            .withBody("\"testString\"")

        expectThat(
            loxoneHttp.get(
                Command(
                    "/test whitespace",
                    Command.Type.JSON,
                    String::class.java,
                    true,
                    false,
                    MiniserverType.KNOWN
                )
            )
        ) {
            isEqualTo("testString")
        }
    }

    @Test
    fun `should follow redirect without auth for DNS endpoint`() {
        val finalLocation = "http://localhost:${Jadler.port()}/final"

        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo("/5039IR9JFSF/jdev/sps/io/test/value")
            .respond()
            .withStatus(302)
            .withHeader("Location", finalLocation)

        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo("/final")
            .respond()
            .withStatus(200)
            .withBody("\"redirectSuccess\"")

        expectThat(
            loxoneHttp.get(
                Command(
                    "/5039IR9JFSF/jdev/sps/io/test/value",
                    Command.Type.JSON,
                    String::class.java,
                    true,
                    false,
                    MiniserverType.KNOWN
                )
            )
        ).isEqualTo("redirectSuccess")

        expectThat(loxoneHttp.lastUrl.toString()).isEqualTo(finalLocation)
    }
}
