package com.hdclark.weddingbingo

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

class MainActivity : Activity() {
    private val game = BingoGame()
    private lateinit var grid: SquareGridLayout
    private lateinit var status: TextView
    private lateinit var newCardButton: Button
    private lateinit var confetti: ConfettiView
    private val cellViews = ArrayList<TextView>(BingoGame.BOARD_SQUARES)
    private var boardLocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#FFF8F6")
        window.navigationBarColor = Color.parseColor("#FFF8F6")
        setContentView(buildUi())
        renderBoard()
        updateStatus()
    }

    private fun buildUi(): View {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#FFF8F6"))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(14), dp(14), dp(14), dp(12))
        }
        root.addView(
            content,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )

        content.addView(
            TextView(this).apply {
                text = "💍 Wedding Bingo 🎉"
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#5E3040"))
                textSize = 28f
                typeface = Typeface.DEFAULT_BOLD
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )

        content.addView(
            TextView(this).apply {
                text = "Tap the clichés you spot. Five in a row wins.\nPlease do not actually object to the marriage. 😄"
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#694F57"))
                textSize = 14f
                setPadding(dp(8), dp(4), dp(8), dp(10))
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )

        grid = SquareGridLayout(this).apply {
            rowCount = BingoGame.BOARD_SIZE
            columnCount = BingoGame.BOARD_SIZE
            alignmentMode = GridLayout.ALIGN_MARGINS
        }
        content.addView(
            grid,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )

        status = TextView(this).apply {
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#5E3040"))
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(8), dp(10), dp(8), dp(8))
        }
        content.addView(
            status,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )

        newCardButton = Button(this).apply {
            text = "New Card ↻"
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9A4D64"))
            setOnClickListener { startFreshCard() }
        }
        content.addView(
            newCardButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52),
            ).apply {
                topMargin = dp(2)
                leftMargin = dp(18)
                rightMargin = dp(18)
            },
        )

        confetti = ConfettiView(this).apply {
            visibility = View.GONE
        }
        root.addView(
            confetti,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )

        return root
    }

    private fun renderBoard() {
        grid.removeAllViews()
        cellViews.clear()

        game.board.forEachIndexed { index, square ->
            val row = index / BingoGame.BOARD_SIZE
            val column = index % BingoGame.BOARD_SIZE
            val cell = TextView(this).apply {
                gravity = Gravity.CENTER
                setPadding(dp(3), dp(3), dp(3), dp(3))
                includeFontPadding = false
                textSize = if (square.isFree) 11f else 9.5f
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                setTextColor(Color.parseColor("#4C3740"))
                isClickable = !square.isFree
                isFocusable = !square.isFree
                if (!square.isFree) {
                    setOnClickListener { onCellTapped(index) }
                }
            }

            cellViews += cell
            grid.addView(
                cell,
                GridLayout.LayoutParams(
                    GridLayout.spec(row, 1f),
                    GridLayout.spec(column, 1f),
                ).apply {
                    width = 0
                    height = 0
                    val margin = dp(2)
                    setMargins(margin, margin, margin, margin)
                },
            )
            updateCell(index)
        }
    }

    private fun onCellTapped(index: Int) {
        if (boardLocked) return
        game.toggle(index)
        cellViews[index].performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        updateCell(index)
        updateStatus()

        if (game.hasBingo()) {
            celebrateBingo()
        }
    }

    private fun updateCell(index: Int) {
        val square = game.board[index]
        val cell = cellViews[index]
        val marked = game.isMarked(index)
        cell.text = if (square.isFree) {
            "💍 FREE\nLove is in the air"
        } else {
            square.prompt?.label.orEmpty()
        }
        cell.background = cellBackground(marked = marked, free = square.isFree)
        cell.alpha = if (marked && !square.isFree) 0.88f else 1f
        cell.contentDescription = when {
            square.isFree -> "Free space, already marked"
            marked -> "Marked: ${square.prompt?.label.orEmpty()}"
            else -> "Not marked: ${square.prompt?.label.orEmpty()}"
        }
    }

    private fun updateStatus() {
        status.text = "${game.markedCount()} of ${BingoGame.PLAYABLE_SQUARES} wedding moments spotted"
    }

    private fun celebrateBingo() {
        boardLocked = true
        status.text = "BINGO! 🎉 A perfectly respectable amount of wedding chaos."
        status.announceForAccessibility("Bingo! Five in a row.")
        newCardButton.isEnabled = false
        confetti.start {
            newCardButton.isEnabled = true
            newCardButton.text = "Play Again 🎊"
            status.text = "BINGO! 🎉 Ready for a fresh card?"
        }
    }

    private fun startFreshCard() {
        confetti.stop()
        game.newCard()
        boardLocked = false
        newCardButton.isEnabled = true
        newCardButton.text = "New Card ↻"
        renderBoard()
        updateStatus()
    }

    private fun cellBackground(marked: Boolean, free: Boolean): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(10).toFloat()
        val fill = when {
            free -> Color.parseColor("#F6D6DF")
            marked -> Color.parseColor("#D8EEDB")
            else -> Color.WHITE
        }
        setColor(fill)
        setStroke(dp(1), Color.parseColor(if (marked) "#6E9C75" else "#D9C6CC"))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()
}

private class SquareGridLayout(context: Context) : GridLayout(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val exactSquare = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        super.onMeasure(exactSquare, exactSquare)
    }
}
