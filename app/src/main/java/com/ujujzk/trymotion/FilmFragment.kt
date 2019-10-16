package com.ujujzk.trymotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_film.*


class FilmFragment: Fragment(), Backable {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_film, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

    override fun goBack() {
        (activity as? MainRouter)?.goToMenu()
    }

}