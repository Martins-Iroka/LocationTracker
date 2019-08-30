package com.martdev.android.androidtracker.locations

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.martdev.android.androidtracker.Injection
import com.martdev.android.androidtracker.MapsActivity
import com.martdev.android.androidtracker.R
import com.martdev.android.androidtracker.data.model.LocationTracker

class LocationsFragment : Fragment(), LocationsContract.View {

    private lateinit var presenter: LocationsPresenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationAdapter
    private lateinit var message: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        presenter = LocationsPresenter(Injection.provideRepo(activity!!), this)

        adapter = LocationAdapter(ArrayList(0))

        presenter.loadLocations()
        presenter.deleteDuplicates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.locations_recycler_view, container, false)

        message = view.findViewById(R.id.empty_view)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.loadLocations()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_all) {
            presenter.deleteLocations()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showLocations(locations: List<LocationTracker>) {
        adapter.setLocations(locations)
        message.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    override fun showNoLocation() {
        recyclerView.visibility = View.GONE
        message.visibility = View.VISIBLE
        message.text = getString(R.string.location_message)
    }

    private inner class LocationHolder internal constructor(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var address: TextView
        private var latitude: TextView
        private var mLocation: LocationTracker? = null
        init {
            itemView.run {
                address = findViewById(R.id.address_view)
                latitude = findViewById(R.id.latitude_longitude)
            }
            itemView.setOnClickListener(this)
        }

        fun bind(location: LocationTracker) {
            mLocation = location
            address.text = location.address
            latitude.text = getString(R.string.lat_long_snippet, location.latitude, location.longitude)
//            latitude.text = location.latitude.toString()
        }

        override fun onClick(v: View?) {
            startActivity(MapsActivity.newIntent(activity!!, mLocation!!.latitude, mLocation!!.longitude, mLocation!!.address))
        }
    }

    private inner class LocationAdapter internal constructor(private var locations: List<LocationTracker>?)
        : RecyclerView.Adapter<LocationHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
            val view = LayoutInflater.from(activity!!).inflate(R.layout.location_item_view, parent, false)
            return LocationHolder(view)
        }

        override fun getItemCount(): Int {
            return if (locations == null) 0 else locations!!.size
        }

        override fun onBindViewHolder(holder: LocationHolder, position: Int) {
            val location = locations!![position]
            holder.bind(location)
        }

        fun setLocations(newLocations: List<LocationTracker>) {
            locations = newLocations
            notifyDataSetChanged()
        }
    }

    companion object {

        fun newInstance(): LocationsFragment {
            return LocationsFragment()
        }
    }
}