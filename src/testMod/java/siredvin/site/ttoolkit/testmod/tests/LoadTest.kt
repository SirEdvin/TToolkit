package siredvin.site.ttoolkit.testmod.tests

import dan200.computercraft.ingame.api.*
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

class LoadTest {
    @GameTest(timeoutTicks = 400)
    fun computerhive(context: GameTestHelper) = context.sequence {
        for (i in 1..8)
            thenComputerOk(i.toString())
        thenExecute {  }
    }
}