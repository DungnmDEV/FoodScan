package com.base.app.baseapp.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.base.app.baseapp.R

class CircularProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress: Int = 0        // Giá trị tiến trình mặc định
    private var maxProgress: Int = 100   // Giá trị tối đa mặc định
    private var circleWidth: Float = 20f // Độ rộng mặc định

    private var backgroundCircleColor: Int = Color.parseColor("#A3A3A3")
    private var foregroundCircleColor: Int = Color.parseColor("#766DE6")

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val foregroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0)
        try {
            circleWidth = typedArray.getDimension(R.styleable.CircularProgressBar_circleWidth, 20f)
            backgroundCircleColor = typedArray.getColor(R.styleable.CircularProgressBar_backgroundCircleColor, backgroundCircleColor)
            foregroundCircleColor = typedArray.getColor(R.styleable.CircularProgressBar_foregroundCircleColor, foregroundCircleColor)
            progress = typedArray.getInt(R.styleable.CircularProgressBar_progress, progress)
            maxProgress = typedArray.getInt(R.styleable.CircularProgressBar_maxProgress, maxProgress)
        } finally {
            typedArray.recycle()
        }

        backgroundPaint.strokeWidth = circleWidth
        backgroundPaint.color = backgroundCircleColor

        foregroundPaint.strokeWidth = circleWidth
        foregroundPaint.color = foregroundCircleColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width - paddingStart - paddingEnd) / 2f - circleWidth / 2f

        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Vẽ background đường tròn
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Vẽ tiến trình
        val angle = 360f * progress / maxProgress
        canvas.drawArc(rectF, -90f, angle, false, foregroundPaint)
    }

    // Phương thức set progress trong code
    fun setProgress(progress: Int) {
        this.progress = progress.coerceIn(0, maxProgress)  // Đảm bảo progress không vượt quá maxProgress
        invalidate()
    }

    // Phương thức set maxProgress trong code
    fun setMaxProgress(maxProgress: Int) {
        this.maxProgress = if (maxProgress > 0) maxProgress else 100  // Đảm bảo maxProgress > 0
        invalidate()
    }
}

