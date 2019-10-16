package com.ujujzk.trymotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_control.*

class ControlFragment: Fragment(), Backable {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        squares_btn.setOnClickListener {
            (activity as MainRouter).goToSquares()
        }
        film_btn.setOnClickListener {
            (activity as MainRouter).goToFilm()
        }
        space_card_btn.setOnClickListener {
            (activity as MainRouter).goToSpaceCard()
        }
        fly_cube_btn.setOnClickListener {
            (activity as MainRouter).goToFluCube()
        }
    }

    override fun goBack() {
        activity?.finish()
    }
}