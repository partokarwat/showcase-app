package com.partokarwat.showcase.utilities

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RethrowingExceptionHandler :
    TestRule,
    Thread.UncaughtExceptionHandler {
    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable,
    ): Nothing = throw UncaughtException(throwable)

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
            }
        }
}

internal class UncaughtException(
    cause: Throwable,
) : Exception(cause)
