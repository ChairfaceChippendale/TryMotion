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

class SeekerMono : View {

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
     * Color of horizontal track.
     */
    var trackColor: Int

    /**
     * Color of the part of the track from [minOpenValue] to [thumbValue].
     */
    var trackLeftOpenColor: Int

    /**
     * Color of the part of the track from [thumbValue] to [maxOpenValue].
     */
    var trackRightOpenColor: Int

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
     * The minimum value on the track.
     */
    var minValue: Int
        set(value) {
            field = value
            minOpenValue = max(minOpenValue, field)
            onValueChanged()
        }

    /**
     * The minimum value thumb can reach
     */
    var minOpenValue: Int
        set(value) {
            field = max(minValue, value)
            field = min(maxOpenValue, field)
            thumbValue = max(thumbValue, field)
            onValueChanged()
        }

    /**
     * Holds the value of thumb.
     */
    private var thumbValue: Int = 0
        set(value) {
            when {
                value < minOpenValue -> {
                    field = minOpenValue
                }
                value > maxOpenValue -> {
                    field = maxOpenValue
                }
                else -> {
                    field = value
                    invalidate()
                }
            }
            onValueChanged()
        }

    /**
     * The maximum value thumb can reach
     */
    var maxOpenValue: Int
        set(value) {
            field = min(value, maxValue)
            field = max(minOpenValue, field)
            thumbValue = min(thumbValue, field)
            onValueChanged()
        }

    /**
     * The maximum value on the track.
     */
    var maxValue: Int
        set(value) {
            field = value
            maxOpenValue = min(maxOpenValue, field)
            onValueChanged()
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


    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {

        val res = context.resources
        val defaultTrackThickness = res.getDimensionPixelSize(R.dimen.rsb_trackDefaultThickness)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SeekerMono, defStyleAttr, 0)

        minValue = a.getInteger(R.styleable.SeekerMono_rm_minValue, 0)
        maxValue = a.getInteger(R.styleable.SeekerMono_rm_maxValue, 100)

        maxOpenValue = a.getInteger(R.styleable.SeekerMono_rm_maxOpenValue, 100)
        minOpenValue = a.getInteger(R.styleable.SeekerMono_rm_minOpenValue, 0)

        thumbValue = a.getInteger(R.styleable.SeekerMono_rm_initialThumbValue, 0)

        sidePadding = a.getDimensionPixelSize(
            R.styleable.SeekerMono_rm_sidePadding,
            res.getDimensionPixelSize(R.dimen.rsb_defaultSidePadding)
        )
        touchRadius = a.getDimensionPixelSize(
            R.styleable.SeekerMono_rm_touchRadius,
            res.getDimensionPixelSize(R.dimen.rsb_touchRadius)
        )
        trackThickness = a.getDimensionPixelSize(
            R.styleable.SeekerMono_rm_trackThickness,
            defaultTrackThickness
        )
        trackColor = a.getColor(
            R.styleable.SeekerMono_rm_trackColor,
            ContextCompat.getColor(context, R.color.rsb_trackDefaultColor)
        )
        trackLeftOpenColor = ContextCompat.getColor(context, R.color.rsb_trackOpenDefaultColor)
        trackRightOpenColor = ContextCompat.getColor(context, R.color.rsb_trackSelectedDefaultColor)

        trackRoundedCaps = a.getBoolean(
            R.styleable.SeekerMono_rm_trackRoundedCaps,
            false
        )

        thumbDrawable = a.getDrawable(R.styleable.SeekerMono_rm_thumbDrawable)
            ?: ContextCompat.getDrawable(context, R.drawable.seeker_thumb_24dp)!!

        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            measureHeight(heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paddingLeft = this.paddingLeft + sidePadding
        val paddingRight = this.paddingRight + sidePadding
        val trackWidth = width - paddingLeft - paddingRight
        val verticalCenter = height / 2f

        val trackXStart = paddingLeft.toFloat()
        val trackXEnd = (paddingLeft + trackWidth).toFloat()

        val valueRange = maxValue - minValue
        val openRange = maxOpenValue - minOpenValue

        val closeLeftRange = minOpenValue - minValue
        val openLeftRange = thumbValue - minOpenValue
        val openRightRange = maxOpenValue - thumbValue
        val closeRightRange = maxValue - maxOpenValue

        val openXStart = paddingLeft.toFloat() + closeLeftRange.toFloat() * trackWidth / valueRange
        val thumbX =
            paddingLeft.toFloat() + (closeLeftRange + openLeftRange).toFloat() * trackWidth / valueRange
        val openXEnd =
            paddingLeft.toFloat() + (closeLeftRange + openLeftRange + openRightRange).toFloat() * trackWidth / valueRange

        // Draw full track
        trackPaint.strokeWidth = trackThickness.toFloat()
        trackPaint.strokeCap = if (trackRoundedCaps) Paint.Cap.ROUND else Paint.Cap.SQUARE
        trackPaint.color = trackColor
        canvas.drawLine(trackXStart, verticalCenter, trackXEnd, verticalCenter, trackPaint)

        //Draw left open track
        trackPaint.color = trackLeftOpenColor
        canvas.drawLine(openXStart, verticalCenter, thumbX, verticalCenter, trackPaint)

        //Draw right open track
        trackPaint.color = trackRightOpenColor
        canvas.drawLine(thumbX, verticalCenter, openXEnd, verticalCenter, trackPaint)

        // Draw thumb at minimumX position
        thumbDrawable.drawAtPosition(canvas, thumbX.toInt())

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var changed = false
        val paddingLeft = this.paddingLeft + sidePadding
        val paddingRight = this.paddingRight + sidePadding
        val width = width - paddingLeft - paddingRight
        val mx = when {
            event.x < paddingLeft -> 0
            paddingLeft <= event.x && event.x <= (this.width - paddingRight) -> ((event.x - paddingLeft) / width * maxValue).toInt()
            else -> maxValue
        }
        val leftThumbX = (paddingLeft + (thumbValue / maxValue.toFloat() * width)).toInt()
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
                thumbValue = mx - offset
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
        if (lastThumbValue != thumbValue) {
            lastThumbValue = thumbValue
            onValueChanged()
        }
        return true
    }


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

    /**
     * Calculates and sets the drawing bounds for drawable and draws it on canvas.
     *
     * @param canvas the canvas to draw on
     * @param position position of the drawable's left edge in horizontal axis (in pixels)
     */
    private fun Drawable.drawAtPosition(
        canvas: Canvas,
        position: Int
    ) {
        val left = position - thumbDrawable.intrinsicWidth / 2
        val top = (height - intrinsicHeight) / 2
        setBounds(left, top, left + intrinsicWidth, top + intrinsicHeight)
        draw(canvas)
    }

    private fun onValueChanged() {
        seekBarChangeListener?.onDrug(
            minValue = minValue,
            minOpenValue = minOpenValue,
            thumbValue = thumbValue,
            maxOpenValue = maxOpenValue,
            maxValue = maxValue
        )
    }


    /**
     * Is used to set callbacks for actions in [SeekerMono]
     */
    interface SeekBarChangeListener {
        /**
         * Gets called when the user has started dragging thumb
         */
        fun onStartedSeeking() {}

        /**
         * Gets called when the user has stopped dragging thumb
         */
        fun onStoppedSeeking() {}

        /**
         * Gets called during the dragging of thumb
         */
        fun onDrug(minValue: Int, minOpenValue: Int,
            thumbValue: Int,
            maxOpenValue: Int, maxValue: Int) {}
    }
}