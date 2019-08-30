package com.martdev.android.androidtracker.location

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.martdev.android.androidtracker.R

class LocationTrackerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)

        val manager = supportFragmentManager

        var fragment = manager.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragment = LocationTrackerFragment.newInstance()
            manager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }
}