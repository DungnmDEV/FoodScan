package com.base.app.baseapp.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import com.base.app.baseapp.R

class IndicatorView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var IndicatorCount = 4
    private var IndicatorSpacing = 20f
    private var IndicatorRadius = 100000f
    private var IndicatorWidth = 0f
    private var IndicatorActive = 0
    private var IndicatorScale = 1
    private var IndicatorPoint = mutableListOf<Float>()
    private var paintDefault = Paint()
    private var paintActive = Paint()
    private var rectIndicator = RectF()

    init {
        val typedArray =
            getContext().theme.obtainStyledAttributes(attrs, R.styleable.IndicatorView, 0, 0)
        try {
            IndicatorCount =
                typedArray.getInteger(R.styleable.IndicatorView_max_count, IndicatorCount)
            IndicatorActive =
                typedArray.getInteger(R.styleable.IndicatorView_active_count, IndicatorActive)
            IndicatorSpacing =
                typedArray.getDimension(R.styleable.IndicatorView_spacing, IndicatorSpacing)
            IndicatorRadius =
                typedArray.getDimension(R.styleable.IndicatorView_radius, IndicatorRadius)
            IndicatorScale =
                typedArray.getInteger(R.styleable.IndicatorView_scale_value, IndicatorScale)
            paintDefault.color = typedArray.getColor(
                R.styleable.IndicatorView_default_color,
                ContextCompat.getColor(getContext(), R.color.black)
            )
            paintActive.color = typedArray.getColor(
                R.styleable.IndicatorView_active_color,
                ContextCompat.getColor(getContext(), R.color.white)
            )
        } finally {
            typedArray.recycle()
        }

        val viewTreeObserver = viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (width > 0 && height > 0) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this)
                        IndicatorWidth = width.toFloat()
                        if (IndicatorCount > 1) {
                            IndicatorWidth =
                                (width - IndicatorSpacing * (IndicatorCount - 1)) / (IndicatorCount - 1 + IndicatorScale)


                            IndicatorPoint.add(0f)

                            for (i in 1 until IndicatorCount) {
                                IndicatorPoint.add((IndicatorSpacing + IndicatorWidth) * i)
                            }
                        }
                        invalidate()
                    }
                }
            })
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (IndicatorCount > 0 && IndicatorPoint.size > 0) {
            for (i in 0 until IndicatorCount) {
                val paint = if (i == IndicatorActive) {
                    paintActive
                } else {
                    paintDefault
                }

                val widthDot = height.toFloat()

                val startPointView =
                    width.toFloat() / 2 - (widthDot * (IndicatorCount - 1) + widthDot * IndicatorScale + IndicatorSpacing * (IndicatorCount - 1))/2

                val startPoint = if (i > IndicatorActive) {
                    (i - 1) * widthDot + IndicatorScale * widthDot + IndicatorSpacing * i + startPointView
                } else {
                    i * widthDot + IndicatorSpacing * i + startPointView
                }
                val indicatorWidth = if (i == IndicatorActive) {
                    widthDot * IndicatorScale
                } else {
                    widthDot
                }
                rectIndicator.set(
                    startPoint,
                    0f,
                    startPoint + indicatorWidth,
                    height.toFloat()
                )

                canvas.drawRoundRect(
                    rectIndicator,
                    IndicatorRadius,
                    IndicatorRadius,
                    paint
                )
            }
        }
    }

    fun setIndicatorMaxCount(count: Int) {
        this.IndicatorCount = count
        invalidate()
    }

    fun setIndicatorActive(number: Int) {
        this.IndicatorActive = number
        invalidate()
    }

    fun setIndicatorDefaultColor(color: Int) {
        this.paintDefault.color = color
        invalidate()
    }

    fun setIndicatorActiveColor(color: Int) {
        this.paintActive.color = color
        invalidate()
    }

    fun setIndicatorRadius(radius: Float) {
        this.IndicatorRadius = radius
        invalidate()
    }

    fun setIndicatorScale(scale: Int) {
        this.IndicatorScale = scale
        invalidate()
    }

    fun setIndicatorSpacing(spacing: Float) {
        this.IndicatorSpacing = spacing
        invalidate()
    }
}
