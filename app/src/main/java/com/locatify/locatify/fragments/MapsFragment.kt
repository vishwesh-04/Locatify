package com.locatify.locatify.fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.locatify.locatify.MainActivity
import com.locatify.locatify.R
import com.locatify.locatify.databinding.FragmentMapsBinding


class MapsFragment : Fragment() {


    var mMap: GoogleMap? = null
    lateinit var mMapBin: FragmentMapsBinding

    private var callback = object : OnMapReadyCallback {
        override fun onMapReady(googleMaps: GoogleMap) {
            var location: Location? = (requireActivity() as MainActivity).getLocation();
            var lat: Double;
            var lng: Double;
            Log.d("MapLocation", location.toString())
            if(location != null) {
                lat = location.latitude
                lng = location.longitude
            }
            else {
                lat = 0.0
                lng = 0.0
            }

            val latlng = LatLng(lat, lng)
            googleMaps.addMarker(MarkerOptions().position(latlng))
            googleMaps.moveCamera(CameraUpdateFactory.newLatLng(latlng))
            googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16f))

            googleMaps.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
                override fun onMapClick(latlng: LatLng) {
                    googleMaps.clear()
                    googleMaps.addMarker(MarkerOptions().position(latlng))
                }

            })

            googleMaps.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                override fun onMarkerClick(marker: Marker): Boolean {

                    AlertDialog.Builder(requireActivity())
                        .setTitle("Confirm Location")
                        .setMessage("Are you sure to select " + marker.position.latitude + " " + marker.position.longitude + " ?")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                val locBundle: Bundle = Bundle()
                                locBundle.putDouble("latitude", marker.position.latitude)
                                locBundle.putDouble("longitude", marker.position.longitude)
                                parentFragmentManager.setFragmentResult("mapLocation", locBundle)
                                Toast.makeText(requireActivity(), locBundle.toString(), Toast.LENGTH_SHORT).show()
                                (requireActivity() as MainActivity).supportFragmentManager.popBackStack()
                            }

                        })
                        .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog?.dismiss()
                            }
                        })
                        .show()
                    return true
                }

            })
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mMapBin = FragmentMapsBinding.inflate(inflater)

        (requireActivity() as MainActivity).supportActionBar?.title = "Select Position"


        return mMapBin.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}