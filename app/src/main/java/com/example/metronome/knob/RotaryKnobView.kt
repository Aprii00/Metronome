package com.example.metronome.knob

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView.ScaleType
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.view.GestureDetectorCompat
import com.example.metronome.R
import kotlinx.android.synthetic.main.rotary_knob_view.view.*
import kotlin.math.atan2

class RotaryKnobView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), GestureDetector.OnGestureListener {
    private val gestureDetector: GestureDetectorCompat
    private var maxValue = resources.getInteger(R.integer.BPMMax)
    private var minValue = resources.getInteger(R.integer.BPMMin)
    private var valueF : Float = resources.getInteger(R.integer.BPMStart).toFloat()
    var value : Int = resources.getInteger(R.integer.BPMStart)
    private var lastAngle: Float = 0f
    private var divider = resources.getInteger(R.integer.divider)
    var listener: RotaryKnobListener? = null
    private var knobDrawable: Drawable? = null


    interface RotaryKnobListener {
        fun onRotate(value: Int)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.rotary_knob_view, this, true)

        knobDrawable = getDrawable(resources, R.drawable.knob, null)
        knobImageView.setImageDrawable(knobDrawable)
        gestureDetector = GestureDetectorCompat(context, this)
        knobImageView.scaleType = ScaleType.MATRIX
    }



    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float)
            : Boolean {
        val angleAfter = calculateAngle(e2.x, e2.y)

        setKnobPosition(angleAfter)

        if(angleAfter - lastAngle > 300){
            lastAngle = 270f
        } else if (angleAfter - lastAngle < -300){
            lastAngle = -90f
        }

//        Log.e("lastAngle", lastAngle.toString())
//        Log.e("angleAfter", angleAfter.toString())
//        Log.e("(angleAfter - lastAngle)", (angleAfter - lastAngle).toString())
//        Log.e("1", valueF.toString())
        valueF += (angleAfter - lastAngle) / divider
//        Log.e("1.2", valueF.toString())
        if(valueF > maxValue) valueF = maxValue.toFloat()
        else if(valueF < minValue) valueF = minValue.toFloat()

        value = valueF.toInt()
//       Log.e("2", valueF.toString())
        if (listener != null) {
            listener!!.onRotate(value)
//            Log.e("3", valueF.toString())
        }

        lastAngle = angleAfter
        return true
    }



    private fun calculateAngle(x: Float, y: Float): Float {
//        Log.e("x", x.toString() + " " + x / width.toFloat())
//        Log.e("y", y.toString() + " " + y / height.toFloat())

        val px = (x / width.toFloat()) - 0.5
        val py = ( 1 - y / height.toFloat()) - 0.5
//        Log.e("deg", Math.toDegrees(atan2(py, px)).toString())
        return -(Math.toDegrees(atan2(py, px))).toFloat() + 90
    }

    private fun setKnobPosition(deg: Float) {
        val matrix = Matrix(this.matrix)
        matrix.postRotate(deg, width.toFloat() / 2, height.toFloat() / 2)
        knobImageView.imageMatrix = matrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event))
            true
        else
            super.onTouchEvent(event)
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {}
    override fun onShowPress(e: MotionEvent?) {}
}
