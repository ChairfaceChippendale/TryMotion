package com.ujujzk.trymotion.seeker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.ujujzk.trymotion.R
import kotlin.math.max
import kotlin.math.min

class SeekerMono: View {
    // region Properties

    /**
     * The paint to draw the horizontal tracks with.
     */
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }


    /**
     * Holds the amount of changed value of a thumb while dragging it.
     */
    private var offset: Int = 0

    /**
     * The thickness of the horizontal track.
     */
    var trackThickness: Int

    /**
     * The thickness of the selected range of horizontal track.
     */
    var trackSelectedThickness: Int

    /**
     * Color of horizontal track.
     */
    var trackColor: Int

    /**
     * Color of the selected range of horizontal track.
     */
    var trackSelectedColor: Int

    /**
     * The acceptable touch radius around thumbs in pixels.
     */
    var touchRadius: Int

    /**
     * The drawable to draw min thumb with.
     */
    var thumbDrawable: Drawable

    /**
     * Side padding for view, by default 16dp on the left and right.
     */
    var sidePadding: Int

    /**
     * If the track should have rounded caps.
     */
    var trackRoundedCaps: Boolean = false

    /**
     * If the selected range track should have rounded caps.
     */
    var trackSelectedRoundedCaps: Boolean = false

    /**
     * Pixel offset of the min thumb
     */
    var thumbOffset: Point


    /**
     * The minimum range to be selected
     */
    var maxReachableValue: Int
        set(value) {
            field = max(value, max)
            thumbValue = min(thumbValue, maxReachableValue)
        }

    /**
     * The maximum value of thumbs which can also be considered as the maximum possible range.
     */
    var max: Int
        set(value) {
            field = value
            maxReachableValue = min(maxReachableValue, field)
            thumbValue = min(maxReachableValue, thumbValue)
        }
    /**
     * Holds the value of min thumb.
     */
    private var thumbValue: Int = 0
        set(value){
            field = min(value, maxReachableValue)
        }


    /**
     * Holds the last value of [thumbValue] in order to send the callback updates
     * only if it is necessary.
     */
    private var lastThumbValue = thumbValue


    /**
     * A callback receiver for view changes.
     */
    var seekBarChangeListener: SeekBarChangeListener? = null


    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val res = context.resources
        val defaultTrackThickness = res.getDimensionPixelSize(R.dimen.rsb_trackDefaultThickness)
        val defaultSidePadding = res.getDimensionPixelSize(R.dimen.rsb_defaultSidePadding)
        val defaultTouchRadius = res.getDimensionPixelSize(R.dimen.rsb_touchRadius)
        val defaultTrackColor = ContextCompat.getColor(context, R.color.rsb_trackDefaultColor)
        val defaultSelectedTrackColor = ContextCompat.getColor(context, R.color.rsb_trackSelectedDefaultColor)
        val defaultThumb = ContextCompat.getDrawable(context, R.drawable.seeker_thumb_24dp)!!

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SeekerMono, 0, 0)
        try {
            max = extractMaxValue(a)
            maxReachableValue = extractMinRange(a)
            sidePadding = extractSidePadding(a, defaultSidePadding)
            touchRadius = extractTouchRadius(a, defaultTouchRadius)
            trackThickness = extractTrackThickness(a, defaultTrackThickness)
            trackSelectedThickness = extractTrackSelectedThickness(a, defaultTrackThickness)
            trackColor = extractTrackColor(a, defaultTrackColor)
            trackSelectedColor = extractTrackSelectedColor(a, defaultSelectedTrackColor)
            thumbDrawable = extractThumbDrawable(a, defaultThumb)
            thumbOffset = extractThumbOffset(a)
            trackRoundedCaps = extractTrackRoundedCaps(a)
            trackSelectedRoundedCaps = extractTrackSelectedRoundedCaps(a)
            val initialThumbValue = extractInitialThumbValue(a)
            if (initialThumbValue != -1) {
                thumbValue = max(0, initialThumbValue)
            }
        } finally {
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paddingLeft = this.paddingLeft + sidePadding
        val paddingRight = this.paddingRight + sidePadding
        val width = width - paddingLeft - paddingRight
        val verticalCenter = height / 2f
        val minimumX = paddingLeft + (thumbValue / max.toFloat()) * width
        val maximumX = paddingLeft.toFloat()

        // Draw full track
        updatePaint(trackThickness, trackColor, trackRoundedCaps)
        canvas.drawLine(paddingLeft + 0f, verticalCenter, paddingLeft + width.toFloat(), verticalCenter, trackPaint)

        // Draw selected range of the track
        updatePaint(trackSelectedThickness, trackSelectedColor, trackSelectedRoundedCaps)
        canvas.drawLine(minimumX, verticalCenter, maximumX, verticalCenter, trackPaint)

        // Draw thumb at minimumX position
        thumbDrawable.drawAtPosition(canvas, minimumX.toInt(), thumbOffset)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var changed = false
        val paddingLeft = this.paddingLeft + sidePadding
        val paddingRight = this.paddingRight + sidePadding
        val width = width - paddingLeft - paddingRight
        val mx = when {
            event.x < paddingLeft -> 0
            paddingLeft <= event.x && event.x <= (this.width - paddingRight) -> ((event.x - paddingLeft) / width * max).toInt()
            else -> max
        }
        val leftThumbX = (paddingLeft + (thumbValue / max.toFloat() * width)).toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInsideRadius(event, leftThumbX, height / 2, touchRadius)) {
                    offset = mx - thumbValue
                    changed = true
                    parent.requestDisallowInterceptTouchEvent(true)
                    seekBarChangeListener?.onStartedSeeking()
                    isPressed = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                thumbValue = max(min(mx - offset, maxReachableValue), 0)
                changed = true
            }
            MotionEvent.ACTION_UP -> {
                seekBarChangeListener?.onStoppedSeeking()
                isPressed = false
            }
        }

        if (!changed) {
            return false
        }

        invalidate()
        if (lastThumbValue != thumbValue ) {
            lastThumbValue = thumbValue
            seekBarChangeListener?.onValueChanged(thumbValue)
        }
        return true
    }

    // region Public functions

    /**
     * Updates the value of minimum thumb and redraws the view.
     */
    fun setMinThumbValue(value: Int) {
        thumbValue = max(value, 0)
        invalidate()
    }

    /**
     * @return the current minimum value of selected range.
     */
    fun getMinThumbValue() = thumbValue







    /**
     * Checks if the given motion event is inside the circle with a radius of [radius] and
     * a center point of {[cx],[cy]}.
     */
    private fun isInsideRadius(event: MotionEvent, cx: Int, cy: Int, radius: Int): Boolean {
        val dx = event.x - cx
        val dy = event.y - cy
        return (dx * dx) + (dy * dy) < (radius * radius)
    }

    /**
     * Updates the stroke width and color of the paint which is used for drawing tracks.
     */
    private fun updatePaint(strokeWidth: Int, color: Int, roundedCaps: Boolean) {
        trackPaint.strokeWidth = strokeWidth.toFloat()
        trackPaint.color = color
        trackPaint.strokeCap = if (roundedCaps) Paint.Cap.ROUND else Paint.Cap.SQUARE
    }

    /**
     * Calculates the height of the view based on the view parameters.
     * If the height is set to []
     */
    @SuppressLint("SwitchIntDef")
    private fun measureHeight(measureSpec: Int): Int {
        val maxHeight = thumbDrawable.intrinsicHeight
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            else -> maxHeight + sidePadding
        }
    }

    // region Extension functions
    /**
     * Calculates and sets the drawing bounds for drawable and draws it on canvas.
     *
     * @param canvas the canvas to draw on
     * @param position position of the drawable's left edge in horizontal axis (in pixels)
     * @param offset the pixel offset of the drawable
     */
    private fun Drawable.drawAtPosition(canvas: Canvas, position: Int, offset: Point = Point(0, 0)) {
        val left = position + offset.x
        val top = ((height - intrinsicHeight) / 2) + offset.y
        setBounds(left, top, left + intrinsicWidth, top + intrinsicHeight)
        draw(canvas)
    }
    // endregion

    // region Attribute extractor functions
    // These functions will extract the view attributes

    private fun extractThumbDrawable(a: TypedArray, defaultValue: Drawable): Drawable {
        return a.getDrawable(R.styleable.SeekerMono_rm_thumbDrawable) ?: defaultValue
    }

    private fun extractTrackSelectedColor(a: TypedArray, defaultValue: Int): Int {
        return a.getColor(R.styleable.SeekerMono_rm_trackSelectedColor, defaultValue)
    }

    private fun extractTrackColor(a: TypedArray, defaultValue: Int): Int {
        return a.getColor(R.styleable.SeekerMono_rm_trackColor, defaultValue)
    }

    private fun extractTrackSelectedThickness(a: TypedArray, defaultValue: Int): Int {
        return a.getDimensionPixelSize(R.styleable.SeekerMono_rm_trackSelectedThickness, defaultValue)
    }

    private fun extractTrackThickness(a: TypedArray, defaultValue: Int): Int {
        return a.getDimensionPixelSize(R.styleable.SeekerMono_rm_trackThickness, defaultValue)
    }

    private fun extractTouchRadius(a: TypedArray, defaultValue: Int): Int {
        return a.getDimensionPixelSize(R.styleable.SeekerMono_rm_touchRadius, defaultValue)
    }

    private fun extractSidePadding(a: TypedArray, defaultValue: Int): Int {
        return a.getDimensionPixelSize(R.styleable.SeekerMono_rm_sidePadding, defaultValue)
    }

    private fun extractTrackRoundedCaps(a: TypedArray): Boolean {
        return a.getBoolean(R.styleable.SeekerMono_rm_trackRoundedCaps, false)
    }

    private fun extractTrackSelectedRoundedCaps(a: TypedArray): Boolean {
        return a.getBoolean(R.styleable.SeekerMono_rm_trackSelectedRoundedCaps, false)
    }

    private fun extractMinRange(a: TypedArray): Int {
        return a.getInteger(R.styleable.SeekerMono_rm_maxReachableValue, 1)
    }

    private fun extractMaxValue(a: TypedArray): Int {
        return a.getInteger(R.styleable.SeekerMono_rm_max, 100)
    }

    private fun extractInitialThumbValue(a: TypedArray): Int {
        return a.getInteger(R.styleable.SeekerMono_rm_initialThumbValue, -1)
    }

    private fun extractThumbOffset(a: TypedArray): Point {
        val x = a.getDimensionPixelSize(R.styleable.SeekerMono_rm_thumbOffsetHorizontal, - thumbDrawable.intrinsicWidth / 2)
        val y = a.getDimensionPixelSize(R.styleable.SeekerMono_rm_thumbOffsetVertical, 0)
        return Point(x, y)
    }


    /**
     * This interface is used to set callbacks for actions in [SeekerMono]
     */
    interface SeekBarChangeListener {
        /**
         * Gets called when the user has started dragging min or max thumbs
         */
        fun onStartedSeeking()

        /**
         * Gets called when the user has stopped dragging min or max thumb
         */
        fun onStoppedSeeking()

        /**
         * Gets called during the dragging of min or max value
         *
         * @param minThumbValue the current minimum value of selected range
         * @param maxThumbValue the current maximum value of selected range
         */
        fun onValueChanged(minValue: Int, minReachableValue: Int, thumbValue: Int, maxReachableValue: Int, maxValue: Int)
    }
}