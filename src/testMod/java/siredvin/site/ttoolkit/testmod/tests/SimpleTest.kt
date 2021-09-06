package siredvin.site.ttoolkit.testmod.tests

import dan200.computercraft.ingame.api.*

class SimpleTest {
    @GameTest
    fun passing(context: GameTestHelper) = context.sequence {
        thenExecute {}
    }

    @GameTest
    fun computer(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }

    @GameTest
    fun computerFail(context: GameTestHelper) = context.sequence {
        thenComputerFail("expected")
    }

    @GameTest(timeoutTicks = 500)
    fun longComputer(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }

    @GameTest
    fun states(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }
}