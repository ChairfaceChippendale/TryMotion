package com.ujujzk.trymotion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity(), MainRouter {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToMenu()
    }

    override fun onBackPressed() {
        (supportFragmentManager.fragments.firstOrNull() as? Backable)?.goBack()
    }

    override fun goToMenu() {
        replaceFragment(ControlFragment())
    }

    override fun goToSquares() {
        replaceFragment(SquaresFragment())
    }

    override fun goToFilm() {
        replaceFragment(FilmFragment())
    }

    override fun goToFluCube() {
        replaceFragment(FlyCubeFragment())
    }

    override fun goToSpaceCard() {
        replaceFragment(SpaceCardFragment())
    }

    override fun goToSeeker() {
        replaceFragment(SeekerFragment())
    }

    private fun replaceFragment(fragment: Fragment){
        if (supportFragmentManager.findFragmentByTag(fragment.javaClass.simpleName) == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                .commit()
        }
    }
}
