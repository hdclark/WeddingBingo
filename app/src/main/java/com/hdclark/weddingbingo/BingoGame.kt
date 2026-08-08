package com.hdclark.weddingbingo

import kotlin.random.Random

data class BingoPrompt(
    val id: String,
    val label: String,
)

data class BingoSquare(
    val prompt: BingoPrompt?,
    val isFree: Boolean = false,
)

class BingoGame(
    private val promptPool: List<BingoPrompt> = WeddingPrompts.all,
    private val random: Random = Random.Default,
) {
    init {
        require(promptPool.size >= PLAYABLE_SQUARES) {
            "At least $PLAYABLE_SQUARES prompts are required"
        }
        require(promptPool.map { it.id }.distinct().size == promptPool.size) {
            "Prompt IDs must be unique"
        }
    }

    var board: List<BingoSquare> = emptyList()
        private set

    private var marked = BooleanArray(BOARD_SQUARES)

    init {
        newCard()
    }

    fun newCard() {
        val prompts = promptPool.shuffled(random).take(PLAYABLE_SQUARES).iterator()
        board = List(BOARD_SQUARES) { index ->
            if (index == FREE_INDEX) {
                BingoSquare(prompt = null, isFree = true)
            } else {
                BingoSquare(prompt = prompts.next())
            }
        }
        marked = BooleanArray(BOARD_SQUARES)
        marked[FREE_INDEX] = true
    }

    fun toggle(index: Int): Boolean {
        require(index in 0 until BOARD_SQUARES) { "Square index out of range" }
        if (index == FREE_INDEX) return false
        marked[index] = !marked[index]
        return marked[index]
    }

    fun isMarked(index: Int): Boolean = marked[index]

    fun markedCount(): Int = marked.count { it } - 1

    fun hasBingo(): Boolean = WINNING_LINES.any { line -> line.all(marked::get) }

    companion object {
        const val BOARD_SIZE = 5
        const val BOARD_SQUARES = BOARD_SIZE * BOARD_SIZE
        const val PLAYABLE_SQUARES = BOARD_SQUARES - 1
        const val FREE_INDEX = 12

        val WINNING_LINES: List<IntArray> = buildList {
            repeat(BOARD_SIZE) { row ->
                add(IntArray(BOARD_SIZE) { column -> row * BOARD_SIZE + column })
            }
            repeat(BOARD_SIZE) { column ->
                add(IntArray(BOARD_SIZE) { row -> row * BOARD_SIZE + column })
            }
            add(IntArray(BOARD_SIZE) { index -> index * (BOARD_SIZE + 1) })
            add(IntArray(BOARD_SIZE) { index -> (index + 1) * (BOARD_SIZE - 1) })
        }
    }
}
