package com.ujujzk.trymotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_seeker.*

class SeekerFragment: Fragment(), Backable {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_seeker, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



    }

    override fun goBack() {
        (activity as? MainRouter)?.goToMenu()
    }
}