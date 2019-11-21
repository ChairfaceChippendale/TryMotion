package com.ujujzk.trymotion.outlined

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.FloatRange

class OutlinedView: View {

    private var progress: Float = 0f
    private val strokeWidth = convertDpToPixel(2f)

    private val paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas) {

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val perimeter = 2 * (viewHeight + viewWidth)
        val crossSize = viewHeight / 2

        paint.color = Color.WHITE

        //draw cross
        //vert
        paint.strokeWidth = strokeWidth
        canvas.drawLine(
            viewWidth - crossSize,
            crossSize / 2 + (progress * crossSize / 2),
            viewWidth - crossSize,
            viewHeight - crossSize / 2 - (progress * crossSize / 2),
            paint)
        //hor
        canvas.drawLine(
            viewWidth - crossSize - crossSize / 2,
            crossSize,
            viewWidth - crossSize / 2,
            crossSize,
            paint)

//        canvas.drawLines()



/*
        //draw stroke
        rectf.set(0f, 0f, viewWidth, viewHeight)
        canvas.drawRoundRect(rectf, timeLineRadious, timeLineRadious, paint)

        //draw progress
        paint.color = ContextCompat.getColor(context, R.color.colorSegProgress)
        val progLineLength = viewWidth * progress / (maxProgress - MIN_VALUE).toFloat()
        rectf.set(0f, 0f, progLineLength, viewHeight)
        canvas.drawRoundRect(rectf, timeLineRadious, timeLineRadious, paint)

*/
    }

    fun setProgress (@FloatRange(from = 0.0, to = 1.0) progress: Float){
        this.progress = progress
        postInvalidate()
    }

    private fun convertDpToPixel(dp: Float): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}