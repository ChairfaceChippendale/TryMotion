package com.ujujzk.trymotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SquaresFragment: Fragment(), Backable {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_squares, container, false)
    }

    override fun goBack() {
        (activity as? MainRouter)?.goToMenu()
    }
}