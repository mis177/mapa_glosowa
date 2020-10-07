package com.example.zadaniedodatkowe

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback  {

    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var marker: Marker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?= inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(context)
        mic.setOnClickListener {
            getSpeech()
        }
        speechText.movementMethod = ScrollingMovementMethod()
        val mapFragment : SupportMapFragment? = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val pw = LatLng(52.2201386, 21.0090806)
        marker = mMap.addMarker(MarkerOptions()
            .position(pw)
            .title("Politechnika Warszawska"))
        val cameraPosition = CameraPosition.Builder()
            .target(pw)
            .zoom(15F)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun getSpeech() {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.address_message))
        try{
            startActivityForResult(mIntent, 10)
        }
        catch(e: Exception){
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10){
            if(resultCode == Activity.RESULT_OK && data != null){
                val speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                speechText.text = speech[0]
                try{
                    val name = geocoder.getFromLocationName(speech[0], 1)
                    speechText.text = name[0].getAddressLine(0)

                    placeMarker(LatLng(name[0].latitude, name[0].longitude), name[0])
                }
                catch(e: Exception){
                    speechText.text = getString(R.string.address_error)
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun placeMarker(position: LatLng, adres: Address) {
        marker.remove()
        var zoom = 10F
        if(adres.thoroughfare != null){
            zoom = 15F
        }
        else if(adres.locality != null){
            zoom = 10F
        }
        else if(adres.featureName != null){
            zoom = 5F
        }
        val cameraPosition = CameraPosition.Builder()
            .target(position)
            .zoom(zoom)
            .build()
        marker = mMap.addMarker(MarkerOptions()
            .position(position)
            .title(adres.getAddressLine(0)))
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}
