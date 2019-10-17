package com.ujujzk.trymotion

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ujujzk.trymotion.seeker.SeekerMono
import kotlinx.android.synthetic.main.fragment_seeker.*

class SeekerFragment: Fragment(), Backable {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_seeker, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        monoSeeker_sk.seekBarChangeListener = object : SeekerMono.SeekBarChangeListener{
            @SuppressLint("SetTextI18n")
            override fun onValueChanged(
                minValue: Int,
                minOpenValue: Int,
                thumbValue: Int,
                maxOpenValue: Int,
                maxValue: Int
            ) {
                seeker_callback.text = """
                    min value $minValue
                    min open $minOpenValue
                    value $thumbValue
                    max open $maxOpenValue
                    max value $maxValue
                """.trimIndent()
            }
        }

        inc_min_open_btn.setOnClickListener {
            monoSeeker_sk.minOpenValue += 5
        }
        red_min_open_btn.setOnClickListener {
            monoSeeker_sk.minOpenValue -= 5
        }
        inc_max_open_btn.setOnClickListener {
            monoSeeker_sk.maxOpenValue += 5
        }
        red_max_open_btn.setOnClickListener {
            monoSeeker_sk.maxOpenValue -= 5
        }

    }

    override fun goBack() {
        (activity as? MainRouter)?.goToMenu()
    }
}