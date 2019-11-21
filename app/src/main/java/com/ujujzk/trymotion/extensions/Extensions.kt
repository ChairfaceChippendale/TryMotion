package com.ujujzk.trymotion.extensions

import androidx.constraintlayout.motion.widget.MotionLayout

fun MotionLayout.setTransitionChangeListener(listener:(Float)-> Unit){
    this.setTransitionListener(object : MotionLayout.TransitionListener{
        override fun onTransitionStarted(layout: MotionLayout?, p1: Int, p2: Int) {
        }

        override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
            listener(progress)
        }

        override fun onTransitionTrigger(layout: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
        }

        override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
        }
    })
}