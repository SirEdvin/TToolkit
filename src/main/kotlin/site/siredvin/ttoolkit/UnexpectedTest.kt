package site.siredvin.ttoolkit

import dan200.computercraft.ingame.api.GameTest
import dan200.computercraft.ingame.api.GameTestHelper
import dan200.computercraft.ingame.api.sequence
import dan200.computercraft.ingame.api.thenExecute

class UnexpectedTest {
    @GameTest
    fun passing(context: GameTestHelper) = context.sequence {
        thenExecute {}
    }
}