package com.martdev.android.androidtracker.locations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.martdev.android.androidtracker.R

class LocationsActivity : AppCompatActivity() {

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, LocationsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)

        val manager = supportFragmentManager
        var fragment = manager.findFragmentById(R.id.fragment_container)

        if (fragment != null) {
            fragment = LocationsFragment.newInstance()
            manager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        } else {
            fragment = LocationsFragment.newInstance()
            manager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }
}