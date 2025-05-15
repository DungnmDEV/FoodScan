package com.base.app.baseapp.custom_view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.base.app.baseapp.R

class CustomSeekbar : BaseSeekbar {

    private var textPaint = TextPaint()
    private var tooltipBackgroundPaint = TextPaint()

    private var textColor = ContextCompat.getColor(context, R.color.white)
    private var textSize = resources.getDimension(R.dimen.medium_text)
    private var topTextAreaPadding = resources.getDimensionPixelSize(R.dimen.val_25dp)

    private var tooltipWidth: Float = 0.0f
    private var tooltipHeightBounds: Int = 0

    private var showTooltipText = false

    private var tooltipBackgroundColor = 0
    private val tooltipRectF = RectF()
    private var progressBackgroundPaint = Paint()
    private var progressForegroundPaint = Paint()
    private var cornerRadius = 20f

    private var fontResId = 0

    private val tooltipRect = Rect()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SeekBarIndicatorProgress, 0, 0)
        recycleAttributes(a)
        init()
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SeekBarIndicatorProgress, 0, 0)
        recycleAttributes(a)
        init()
    }

    private fun recycleAttributes(a: TypedArray) {
        try {
            attributes(a)
        } finally {
            a.recycle()
        }
    }

    private fun attributes(a: TypedArray) {
        textColor = a.getColor(R.styleable.SeekBarIndicatorProgress_IndicatorTextColor,
            ContextCompat.getColor(context, R.color.white)
        )
        textSize = a.getDimension(
            R.styleable.SeekBarIndicatorProgress_IndicatorTextSize,
            resources.getDimension(R.dimen.medium_text)
        )
        topTextAreaPadding = a.getDimensionPixelSize(
            R.styleable.SeekBarIndicatorProgress_IndicatorTextPadding,
            resources.getDimensionPixelSize(R.dimen.val_25dp)
        )
        showTooltipText = a.getBoolean(R.styleable.SeekBarIndicatorProgress_showTooltipText, false)

        tooltipBackgroundColor = a.getColor(
            R.styleable.SeekBarIndicatorProgress_IndicatorBackgroundColor,
            ContextCompat.getColor(context, R.color.black)
        )
        fontResId = a.getResourceId(R.styleable.SeekBarIndicatorProgress_IndicatorTextFont, 0)
        steps = a.getColor(R.styleable.SeekBarIndicatorProgress_steps, 1)
    }

    private fun init() {
        initDefaultAttributes()
        initPaintProperties()
        initProgressBarPaints()

        setOnSeekBarChangeListener(this)

        tooltipWidth = textPaint.measureText(progress.toString())
        textPaint.getTextBounds(progress.toString(), 0, progress.toString().length, tooltipRect)
        tooltipHeightBounds = tooltipRect.height()
    }

    private fun initPaintProperties() {
        textPaint.flags = Paint.ANTI_ALIAS_FLAG
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize
        if (fontResId != 0) {
            val typeface = ResourcesCompat.getFont(context, fontResId)
            textPaint.typeface = typeface
        }

        tooltipBackgroundPaint.flags = Paint.ANTI_ALIAS_FLAG
        tooltipBackgroundPaint.color = tooltipBackgroundColor
        tooltipBackgroundPaint.style = Paint.Style.STROKE
        tooltipBackgroundPaint.strokeWidth = 5f
    }

    private fun initProgressBarPaints() {
        progressBackgroundPaint.apply {
            color = Color.parseColor("#412D7E")  // Màu nền
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        progressForegroundPaint.apply {
            color = Color.parseColor("#906CFF")  // Màu tiến trình
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    private fun initDefaultAttributes() {
        if (showTooltipText) {
            setPadding(paddingLeft, topTextAreaPadding, paddingRight, paddingBottom)
        }
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {

        val width = width.toFloat()
        val height = height.toFloat()
        val progressRatio = progress / max.toFloat()
        val backgroundRect =
            RectF(0f + paddingLeft, height - 30f, width - paddingRight, height - 10f)
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, progressBackgroundPaint)

        val progressRect = RectF(
            0f + paddingLeft,
            height - 30f,
            paddingLeft + (width - paddingLeft - paddingRight) * progressRatio,
            height - 10f,
        )
        canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressForegroundPaint)

        if (!showTooltipText) {
            return
        }

        val x = thumb.current.bounds.centerX().toFloat()
        val y =
            tooltipHeightBounds.toFloat() + tooltipHeightBounds / 3 + 5f / 2

        val startOfTooltip = x - tooltipWidth / 2 + paddingLeft - thumb.minimumWidth / 2
        val endOfTooltip = x + tooltipWidth / 2 + paddingRight - thumb.minimumWidth / 2

        when {
            startOfTooltip < paddingLeft -> {
                val centerX = x - startOfTooltip + paddingLeft + thumb.minimumWidth / 6

                drawOutline(canvas, centerX - tooltipWidth / 2, centerX + tooltipWidth / 2, y)
                drawText(canvas, centerX, y)
            }

            endOfTooltip > width - paddingRight -> {
                val centerX =
                    x - (endOfTooltip - width) - (thumb.minimumWidth / 2) + (thumb.minimumWidth / 3)

                drawOutline(canvas, centerX - tooltipWidth / 2, centerX + tooltipWidth / 2, y)
                drawText(canvas, centerX, y)
            }

            else -> {
                val centerX = x + (thumb.minimumWidth / 2) - thumb.minimumWidth / 8

                drawOutline(canvas, startOfTooltip, endOfTooltip, y)
                drawText(canvas, centerX, y)
            }
        }
    }

    private fun drawText(canvas: Canvas, x: Float, y: Float) {
        canvas.drawText("$progress%", x, y, textPaint)
    }
    private fun drawOutline(canvas: Canvas, left: Float, right: Float, y: Float) {
        val offset = tooltipHeightBounds / 3

        tooltipRectF.set(
            left - offset - 20,
            y - tooltipHeightBounds - offset,
            right + offset + 20,
            y + offset / 3 + offset
        )
        tooltipBackgroundPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(tooltipRectF, 10f, 10f, tooltipBackgroundPaint)


        val trianglePath = Path().apply {
            moveTo((left + right) / 2 - 20, y)
            lineTo((left + right) / 2 + 20, y)
            lineTo((left + right) / 2, y + 30)
            close()
        }
        canvas.drawPath(trianglePath, tooltipBackgroundPaint)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        this.progress = addStepsIfRequired(progress)
        tooltipWidth = textPaint.measureText(progress.toString())
    }
}