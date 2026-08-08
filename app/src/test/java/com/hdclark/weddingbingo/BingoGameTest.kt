package com.hdclark.weddingbingo

import kotlin.math.pow
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BingoGameTest {
    @Test
    fun cardHasTwentyFourUniquePromptsAndFreeCenter() {
        val game = BingoGame(random = Random(7))
        val prompts = game.board.mapNotNull { it.prompt }

        assertEquals(BingoGame.BOARD_SQUARES, game.board.size)
        assertEquals(BingoGame.PLAYABLE_SQUARES, prompts.size)
        assertEquals(prompts.size, prompts.map { it.id }.distinct().size)
        assertTrue(game.board[BingoGame.FREE_INDEX].isFree)
        assertTrue(game.isMarked(BingoGame.FREE_INDEX))
        assertEquals(0, game.markedCount())
    }

    @Test
    fun tappingSquareTogglesItOnAndOff() {
        val game = BingoGame(random = Random(11))

        assertFalse(game.isMarked(0))
        assertTrue(game.toggle(0))
        assertTrue(game.isMarked(0))
        assertFalse(game.toggle(0))
        assertFalse(game.isMarked(0))
    }

    @Test
    fun everyWinningLineIsRecognized() {
        BingoGame.WINNING_LINES.forEachIndexed { lineIndex, line ->
            val game = BingoGame(random = Random(lineIndex))
            line.filter { it != BingoGame.FREE_INDEX }.forEach { game.toggle(it) }
            assertTrue("Winning line $lineIndex was not recognized", game.hasBingo())
        }
    }

    @Test
    fun fourSquaresWithoutAFreeSpaceDoNotWin() {
        val game = BingoGame(random = Random(23))
        intArrayOf(0, 1, 2, 3).forEach { game.toggle(it) }
        assertFalse(game.hasBingo())
    }

    @Test
    fun newCardClearsMarksAndKeepsFreeSpaceMarked() {
        val game = BingoGame(random = Random(31))
        game.toggle(0)
        game.toggle(1)

        game.newCard()

        assertEquals(0, game.markedCount())
        assertTrue(game.isMarked(BingoGame.FREE_INDEX))
        assertFalse(game.isMarked(0))
    }

    @Test
    fun fiveGamesExposeRoughlyHalfThePromptPoolInExpectation() {
        val poolSize = WeddingPrompts.all.size.toDouble()
        val chanceMissedInOneGame = (poolSize - BingoGame.PLAYABLE_SQUARES) / poolSize
        val expectedSeenFraction = 1.0 - chanceMissedInOneGame.pow(5)

        assertTrue(
            "Expected five-game exposure was $expectedSeenFraction for ${WeddingPrompts.all.size} prompts",
            expectedSeenFraction in 0.45..0.55,
        )
    }
}
