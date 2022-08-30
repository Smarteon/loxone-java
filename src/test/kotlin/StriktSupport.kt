package cz.smarteon.loxone

import strikt.api.Assertion

internal fun <T> Assertion.Builder<T>.isLoxoneUuid(uuid: String): Assertion.Builder<T> =
    assert("is Loxone UUID ${LoxoneUuid(uuid)}") {
        if (it == LoxoneUuid(uuid)) pass()
        else fail()
    }
