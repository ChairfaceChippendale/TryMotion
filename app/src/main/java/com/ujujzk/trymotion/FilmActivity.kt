package com.ujujzk.trymotion

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_space_card.*

class FilmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film)

        motion_container.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                if(currentId == R.id.set_1) {
                    motion_container.postDelayed({
                        motion_container.transitionToState(R.id.set_2)
                    }, 100)

                }
            }
        })

    }

    fun start(view: View){
        motion_container.transitionToState(R.id.set_1)
    }

}