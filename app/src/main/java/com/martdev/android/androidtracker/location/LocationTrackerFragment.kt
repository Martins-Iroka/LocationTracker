package com.martdev.android.androidtracker.location

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.martdev.android.androidtracker.Injection
import com.martdev.android.androidtracker.R
import com.martdev.android.androidtracker.locations.LocationsActivity

class LocationTrackerFragment : Fragment(), LocationTrackerContract.View {

    private lateinit var presenter: LocationTrackerPresenter
    private lateinit var locationText: TextView
    private lateinit var start_stop_button: Button
    private lateinit var locations: Button
    private lateinit var place: ImageView
    private lateinit var locationCallback: LocationCallback

    private var isTracking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = LocationTrackerPresenter(activity!!, Injection.provideRepo(activity!!), this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.android_tracker_main, container, false)

        place = view.findViewById(R.id.place)
        place.visibility = View.INVISIBLE

        val animatorSet = AnimatorInflater.loadAnimator(activity, R.animator.rotate) as AnimatorSet
        animatorSet.setTarget(place)

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                if (isTracking) presenter.loadResult(p0!!.lastLocation)
            }
        }

        locationText = view.findViewById(R.id.location)
        start_stop_button = view.findViewById(R.id.start_stop_tracker)
        start_stop_button.setOnClickListener {
            isTracking = !isTracking
            if (isTracking) {
                presenter.startTracker(isTracking, locationCallback)
                locations.isEnabled = false
                place.visibility = View.VISIBLE
                animatorSet.start()
            }
            else {
                presenter.stopTracker()
                locations.isEnabled = true
                place.visibility = View.INVISIBLE
                animatorSet.end()
            }
        }

        locations = view.findViewById(R.id.location_history)
        locations.setOnClickListener {
            startActivity(LocationsActivity.newIntent(activity!!))
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (isTracking) {
            presenter.startTracker(isTracking, locationCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isTracking) {
            presenter.stopTracker()
            isTracking = true
        }
    }

    override fun showHistory() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showRequestPermission() {
        requestPermissions(REQUEST_PERMISSION, PERMISSION_CODE)
    }

    override fun showLoadingIcon() {
        start_stop_button.text = getString(R.string.stop_tracker)
    }

    override fun stopLoadingIcon() {
        start_stop_button.text = getString(R.string.start_tracker)
        isTracking = false
    }

    override fun showResult(address: String) {
        locationText.text = address
    }

    companion object {

        private val REQUEST_PERMISSION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        private const val PERMISSION_CODE = 100

        fun newInstance(): LocationTrackerFragment {
            return LocationTrackerFragment()
        }
    }
}