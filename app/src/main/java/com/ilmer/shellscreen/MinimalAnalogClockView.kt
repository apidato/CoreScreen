package com.ilmer.shellscreen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.Calendar

class MinimalAnalogClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private var clockColor = Color.GREEN

    fun setClockColor(color: Int) {
        clockColor = color
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val size = if (parentWidth > 0) (parentWidth * 0.8f).toInt() else 800
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = (Math.min(width, height) / 2f) * 0.85f
        val centerX = width / 2f
        val centerY = height / 2f

        // Marcas de horas (sin números)
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30).toDouble())
            val sin = Math.sin(angle).toFloat()
            val cos = Math.cos(angle).toFloat()

            val startX = centerX + (radius * 0.80f * sin)
            val startY = centerY - (radius * 0.80f * cos)
            val endX = centerX + (radius * 0.95f * sin)
            val endY = centerY - (radius * 0.95f * cos)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            paint.color = clockColor
            canvas.drawLine(startX, startY, endX, endY, paint)
        }

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR).toFloat()
        val minute = calendar.get(Calendar.MINUTE).toFloat()
        val second = calendar.get(Calendar.SECOND).toFloat()

        // Manecilla de Horas
        val hourAngle = Math.toRadians(((hour + minute / 60f) * 30f).toDouble())
        val hourX = centerX + (radius * 0.40f * Math.sin(hourAngle)).toFloat()
        val hourY = centerY - (radius * 0.40f * Math.cos(hourAngle)).toFloat()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = clockColor
        canvas.drawLine(centerX, centerY, hourX, hourY, paint)

        // Manecilla de Minutos
        val minuteAngle = Math.toRadians(((minute + second / 60f) * 6f).toDouble())
        val minuteX = centerX + (radius * 0.65f * Math.sin(minuteAngle)).toFloat()
        val minuteY = centerY - (radius * 0.65f * Math.cos(minuteAngle)).toFloat()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.color = clockColor
        canvas.drawLine(centerX, centerY, minuteX, minuteY, paint)

        // Segundero más corto, sutil y del mismo color
        val secondAngle = Math.toRadians((second * 6f).toDouble())
        val secondX = centerX + (radius * 0.55f * Math.sin(secondAngle)).toFloat()
        val secondY = centerY - (radius * 0.55f * Math.cos(secondAngle)).toFloat()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = clockColor
        canvas.drawLine(centerX, centerY, secondX, secondY, paint)

        // Centro
        paint.style = Paint.Style.FILL
        paint.color = clockColor
        canvas.drawCircle(centerX, centerY, 6f, paint)
    }
}