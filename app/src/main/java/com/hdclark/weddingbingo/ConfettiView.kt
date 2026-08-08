package com.hdclark.weddingbingo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin
import kotlin.random.Random

class ConfettiView(context: Context) : View(context) {
    private data class Particle(
        val x: Float,
        val startY: Float,
        val speed: Float,
        val sway: Float,
        val size: Float,
        val rotation: Float,
        val color: Int,
    )

    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#5E3040")
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    private val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#694F57")
        textAlign = Paint.Align.CENTER
    }
    private val particles = ArrayList<Particle>()
    private var progress = 0f
    private var animator: ValueAnimator? = null

    fun start(onFinished: () -> Unit) {
        animator?.cancel()
        createParticles()
        visibility = VISIBLE
        alpha = 1f
        progress = 0f

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3200L
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                private var cancelled = false

                override fun onAnimationCancel(animation: Animator) {
                    cancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    visibility = GONE
                    animator = null
                    if (!cancelled) onFinished()
                }
            })
            start()
        }
    }

    fun stop() {
        animator?.cancel()
        animator = null
        visibility = GONE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.argb(105, 40, 24, 31))

        val w = width.toFloat()
        val h = height.toFloat()
        particles.forEachIndexed { index, particle ->
            val travel = particle.startY + progress * particle.speed
            val y = (travel % 1.35f) * h
            val x = (particle.x + sin(progress * 14f + index) * particle.sway) * w
            particlePaint.color = particle.color
            canvas.save()
            canvas.rotate(progress * particle.rotation, x, y)
            canvas.drawRoundRect(
                x - particle.size,
                y - particle.size * 0.45f,
                x + particle.size,
                y + particle.size * 0.45f,
                particle.size * 0.25f,
                particle.size * 0.25f,
                particlePaint,
            )
            canvas.restore()
        }

        val panelWidth = w * 0.78f
        val panelHeight = dp(132).toFloat()
        val panel = RectF(
            (w - panelWidth) / 2f,
            (h - panelHeight) / 2f,
            (w + panelWidth) / 2f,
            (h + panelHeight) / 2f,
        )
        canvas.drawRoundRect(panel, dp(24).toFloat(), dp(24).toFloat(), panelPaint)

        titlePaint.textSize = dp(34).toFloat()
        subtitlePaint.textSize = dp(16).toFloat()
        canvas.drawText("BINGO! 🎉", w / 2f, h / 2f - dp(8), titlePaint)
        canvas.drawText("Five in a row. Fancy!", w / 2f, h / 2f + dp(28), subtitlePaint)
    }

    private fun createParticles() {
        val random = Random(System.nanoTime())
        val palette = intArrayOf(
            Color.parseColor("#E85D75"),
            Color.parseColor("#F4B942"),
            Color.parseColor("#6DBA8A"),
            Color.parseColor("#6A8DFF"),
            Color.parseColor("#B57EDC"),
            Color.parseColor("#F08DB6"),
        )
        particles.clear()
        repeat(72) {
            particles += Particle(
                x = random.nextFloat(),
                startY = -random.nextFloat() * 0.9f,
                speed = 1.15f + random.nextFloat() * 1.2f,
                sway = 0.02f + random.nextFloat() * 0.05f,
                size = dp(4 + random.nextInt(5)).toFloat(),
                rotation = 260f + random.nextFloat() * 620f,
                color = palette[random.nextInt(palette.size)],
            )
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
