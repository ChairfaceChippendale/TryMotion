package com.ujujzk.trymotion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_fly_cube.*
import kotlin.math.ceil


/**
 * https://code.tutsplus.com/ru/tutorials/creating-animations-with-motionlayout-for-android--cms-31497
 */
class FlyCubeFragment : Fragment(), Backable {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fly_cube, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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


    override fun goBack() {
        (activity as? MainRouter)?.goToMenu()
    }

}
