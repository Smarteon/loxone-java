package cz.smarteon.loxone

import strikt.api.Assertion
import java.util.Calendar
import java.util.Date
import kotlin.reflect.KClass

private val classLoader = object {}.javaClass.enclosingClass

internal fun <T : Any> readResource(path: String, type: KClass<T>): T =
    classLoader.getResourceAsStream(sanitizePath(path)).use { stream ->
        Codec.readMessage(stream, type.java)
    }

internal fun <T : Any> readResourceXml(path: String, type: KClass<T>): T =
    classLoader.getResourceAsStream(sanitizePath(path)).use { stream ->
        Codec.readXml(stream, type.java)
    }

internal fun <T> Assertion.Builder<T>.isLoxoneUuid(uuid: String): Assertion.Builder<T> =
    assert("is Loxone UUID ${LoxoneUuid(uuid)}") {
        if (it == LoxoneUuid(uuid)) pass()
        else fail()
    }

internal fun <T : Any> readValue(value: String, type: KClass<T>): T = Codec.readMessage(value, type.java)

internal fun writeValue(value: Any): String = Codec.writeMessage(value)

internal fun getDate(): Date = Calendar.getInstance().let { cal -> cal.set(Calendar.MILLISECOND, 0); cal.time }

internal fun formatDate(date: Date): String = Codec.DATE_FORMAT.format(date)

internal fun parseDate(date: String): Date = Codec.DATE_FORMAT.parse(date)

private fun sanitizePath(path: String): String = if (path.startsWith("/")) path else "/$path"
