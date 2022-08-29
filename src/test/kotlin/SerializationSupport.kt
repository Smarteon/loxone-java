package cz.smarteon.loxone

import java.io.InputStream
import java.util.Calendar
import java.util.Date

private val classLoader = object {}.javaClass.enclosingClass

internal inline fun <reified T : Any> readResource(path: String): T =
    readResource<T>(path) { stream ->
        Codec.readMessage(stream, T::class.java)
    }

internal inline fun <reified T : Any> readResourceXml(path: String): T =
    readResource<T>(path) { stream ->
        Codec.readXml(stream, T::class.java)
    }

private fun <T : Any> readResource(path: String, reader: (InputStream) -> T): T =
    classLoader.getResourceAsStream((sanitizePath(path)))?.use(reader)
        ?: throw IllegalArgumentException("Resource $path not found")

internal inline fun < reified T : Any> readValue(value: String,): T = Codec.readMessage(value, T::class.java)

internal fun writeValue(value: Any): String = Codec.writeMessage(value)

internal fun getDate(): Date = Calendar.getInstance().let { cal -> cal.set(Calendar.MILLISECOND, 0); cal.time }

internal fun formatDate(date: Date): String = Codec.DATE_FORMAT.format(date)

internal fun parseDate(date: String): Date = Codec.DATE_FORMAT.parse(date)

private fun sanitizePath(path: String): String = if (path.startsWith("/")) path else "/$path"
