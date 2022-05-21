package cz.smarteon.loxone

import kotlin.reflect.KClass

private val classLoader = object{}.javaClass.enclosingClass

internal fun <T : Any> readResource(path: String, type: KClass<T>): T {
    val stream = classLoader.getResourceAsStream(sanitizePath(path))
    return Codec.readMessage(stream, type.java)
}

private fun sanitizePath(path: String): String = if (path.startsWith("/")) path else "/$path"
