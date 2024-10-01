package com.example.tarea1grupog3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.tarea1grupog3.R
import com.example.tarea1grupog3.databases.DatabaseHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class FragmentLugares : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var spinner: Spinner

    private lateinit var dbHelper: DatabaseHelper

    private var lugares = listOf<Pair<String, LatLng>>()

    private var currentMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lugares, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        lugares = dbHelper.getAllLugares()


        // Obtener el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar el Spinner
        spinner = view.findViewById(R.id.spinnerLugares)
        val lugaresNombres = lugares.map { it.first }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lugaresNombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Listener para el Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val lugarSeleccionado = lugares[position]
                val coordenada = lugarSeleccionado.second
                coordenada?.let {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    addMarker(it, lugarSeleccionado.first)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    // Método para añadir el marcador en el mapa
    private fun addMarker(location: LatLng, title: String) {
        currentMarker?.remove() // Eliminar el marcador actual si existe
        currentMarker = mMap.addMarker(MarkerOptions().position(location).title(title))
    }
}
