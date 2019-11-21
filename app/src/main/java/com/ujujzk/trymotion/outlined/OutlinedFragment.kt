package com.ujujzk.trymotion.outlined

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ujujzk.trymotion.Backable
import com.ujujzk.trymotion.MainRouter
import com.ujujzk.trymotion.R
import com.ujujzk.trymotion.extensions.setTransitionChangeListener
import kotlinx.android.synthetic.main.fragment_control.*
import kotlinx.android.synthetic.main.fragment_outlined.*
import kotlinx.android.synthetic.main.fragment_outlined.motion_container


class OutlinedFragment: Fragment(), Backable {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_outlined, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        motion_container.setTransitionChangeListener { progress ->
            stroke_btn.setProgress(progress)
        }
    }

    override fun goBack() {
        (activity as? MainRouter)?.goToMenu()
    }
}