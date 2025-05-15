package com.base.app.baseapp.custom_view

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

abstract class BaseSeekbar : AppCompatSeekBar, SeekBar.OnSeekBarChangeListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    protected var steps = 1

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    protected fun addStepsIfRequired(progress: Int): Int {
        val stepProgress = ((progress / steps) * steps)
        return round(stepProgress)
    }

    private fun round(n: Int): Int {
        val firstNumber = n / steps * steps
        val secondNumber = firstNumber + steps
        return if (n - firstNumber > secondNumber - n) secondNumber else firstNumber
    }
}