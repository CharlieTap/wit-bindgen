//@ args = [
//@   '--with=test:kotlin-with/mapped=generate',
//@   '--with=test:kotlin-with/mapped/point=external.Point',
//@   '--with=test:kotlin-with/mapped/status=external.Status',
//@   '--with=test:kotlin-with/mapped/token=external.Token',
//@   '--with=test:kotlin-with/mapped/value=external.Value',
//@   '--with=test:kotlin-with/mapped/generated=generate',
//@   '--with=test:kotlin-with/shared=external.Shared',
//@ ]

package external

import bindings.runtime.ResourceHandle

data class Point(val x: UInt, val y: UInt)

enum class Status {
    READY,
    PENDING,
}

class Token internal constructor(internal var __handle: ResourceHandle)

sealed interface Value {
    data class Point(val value: external.Point) : Value
    data class Status(val value: external.Status) : Value
    data class Token(val value: external.Token) : Value
}

object Shared {
    data class Payload(val value: UInt)
}
