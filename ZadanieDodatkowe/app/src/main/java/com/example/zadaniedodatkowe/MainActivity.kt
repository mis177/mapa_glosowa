package com.example.zadaniedodatkowe


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment(MapFragment())
    }

    private fun showFragment(
        fragment: Fragment
    ) {
        supportFragmentManager.beginTransaction().run {
            setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
            replace(R.id.main_frame, fragment)
            commit()
        }
    }
}

