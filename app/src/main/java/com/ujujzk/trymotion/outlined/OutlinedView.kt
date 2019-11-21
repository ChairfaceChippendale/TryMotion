package com.ujujzk.trymotion.outlined

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.FloatRange

class OutlinedView: View {

    private var progress: Float = 0f
    private val strokeWidth = convertDpToPixel(2f)

    val start = PointFloat()
    val stop = PointFloat()
    val corner0 = PointFloat()
    val corner1 = PointFloat()
    val corner2 = PointFloat()
    val corner3 = PointFloat()
    val corner4 = PointFloat()

    val pointList = ArrayList<PointFloat>()
    val toDraw = Path()


    private val paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas) {
        pointList.clear()

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val perimeter = 2 * (viewHeight + viewWidth)
        val crossSize = viewHeight / 2

        paint.color = Color.WHITE

        //draw cross
        //vert
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        canvas.drawLine(
            viewWidth - crossSize,
            crossSize / 2 + (progress * crossSize / 2),
            viewWidth - crossSize,
            viewHeight - crossSize / 2 - (progress * crossSize / 2),
            paint)


        corner0.x = viewWidth
        corner0.y = viewHeight / 2f
        corner1.x = viewWidth
        corner1.y = 0f
        corner2.x = 0f
        corner2.y = 0f
        corner3.x = 0f
        corner3.y = viewHeight
        corner4.x = viewWidth
        corner4.y = viewHeight



        val stopTotal = .25f * viewHeight + .5f * viewHeight + viewWidth + viewHeight + viewWidth + viewHeight

        val stopPart1 = .25f * viewHeight / stopTotal
        val stopPart2 = (.5f * viewHeight) / stopTotal
        val stopPart3 = (viewWidth) / stopTotal
        val stopPart4 = (viewHeight) / stopTotal
        val stopPart5 = (viewWidth) / stopTotal
        val stopPart6 = (viewHeight) / stopTotal

        if (progress < stopPart1) {
            val partProgress = progress / stopPart1
            stop.x = viewWidth - crossSize / 2 + (crossSize / 2) * partProgress
            stop.y = viewHeight / 2

        }
        else if (progress < stopPart1 + stopPart2) {
            val partProgress= (progress - stopPart1) / stopPart2
            stop.x = viewWidth
            stop.y = viewHeight / 2 - (viewHeight / 2) * partProgress
            pointList.add(corner0)
        }
        else if (progress < stopPart1 + stopPart2 + stopPart3) {
            val partProgress = (progress - stopPart1 - stopPart2) / stopPart3
            stop.x = viewWidth - viewWidth * partProgress
            stop.y = 0f
            pointList.add(corner0)
            pointList.add(corner1)
        }
        else if (progress < stopPart1 + stopPart2 + stopPart3 + stopPart4) {
            val partProgress = (progress - stopPart1 - stopPart2 - stopPart3) / stopPart4
            stop.x = 0f
            stop.y = viewHeight * partProgress
            pointList.add(corner0)
            pointList.add(corner1)
            pointList.add(corner2)
        }
        else if (progress < stopPart1 + stopPart2 + stopPart3 + stopPart4 + stopPart5) {
            val partProgress = (progress - stopPart1 - stopPart2 - stopPart3 - stopPart4) / stopPart5
            stop.x = viewWidth * partProgress
            stop.y = viewHeight
            pointList.add(corner0)
            pointList.add(corner1)
            pointList.add(corner2)
            pointList.add(corner3)
        }
        else {
            val partProgress = (progress - stopPart1 - stopPart2 - stopPart3 - stopPart4 - stopPart5) / stopPart6
            stop.x = viewWidth
            stop.y = viewHeight - viewHeight * partProgress
            pointList.add(corner0)
            pointList.add(corner1)
            pointList.add(corner2)
            pointList.add(corner3)
            pointList.add(corner4)
        }


        val startTotal = .75f * crossSize + viewHeight / 2
        val startPart1 = .75f * crossSize / startTotal
        val startPart2 = viewHeight / 2 / startTotal


        if  (progress < startPart1){
            //first path part
            val partProgress = progress / startPart1

            start.x = viewWidth - crossSize - crossSize / 2 + (crossSize + crossSize / 2) * partProgress
            start.y = crossSize

        } else {
            //second path part
            val partProgress = (progress - startPart1) / startPart2

            start.x = viewWidth
            start.y = crossSize - crossSize * partProgress

            pointList.remove(corner0)

        }


        toDraw.rewind()

        toDraw.moveTo(start.x, start.y)

        pointList.add(stop)

        pointList.forEach {
            toDraw.lineTo(it.x, it.y)
        }


        canvas.drawPath(toDraw, paint)


        canvas.drawPoint(start.x, start.y, paint)
        canvas.drawPoint(stop.x, stop.y, paint)

    }

    fun setProgress (@FloatRange(from = 0.0, to = 1.0) progress: Float){
        this.progress = progress
        postInvalidate()
    }

    private fun convertDpToPixel(dp: Float): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    inner class PointFloat{

        var x: Float = 0f
        var y: Float = 0f
    }
}