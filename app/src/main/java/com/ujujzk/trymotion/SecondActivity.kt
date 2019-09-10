package com.ujujzk.trymotion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.motion.*
import kotlin.math.ceil


/**
 * https://code.tutsplus.com/ru/tutorials/creating-animations-with-motionlayout-for-android--cms-31497
 */
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.motion)

        motion_container.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(layout: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                progress_bar.progress = ceil(progress * 100).toInt()
            }

            override fun onTransitionTrigger(layout: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

            override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
                if(currentId == R.id.ending_set) {
                    motion_container.transitionToStart()
                }
            }
        })
    }

//    fun start(v: View){
//        motion_container.transitionToEnd()
//    }

}
