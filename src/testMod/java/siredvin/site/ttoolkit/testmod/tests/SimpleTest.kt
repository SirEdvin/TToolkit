package siredvin.site.ttoolkit.testmod.tests

import dan200.computercraft.ingame.api.*
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

class SimpleTest {
    @GameTest
    fun passing(context: GameTestHelper) = context.sequence {
        thenExecute {}
    }

    @GameTest(setupTicks = 5, timeoutTicks = 200)
    fun computer(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }

    @GameTest(setupTicks = 5, timeoutTicks = 200)
    fun computerFail(context: GameTestHelper) = context.sequence {
        thenComputerFail("expected")
    }

    @GameTest(timeoutTicks = 500, setupTicks = 5)
    fun longComputer(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }
}