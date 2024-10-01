package com.example.tarea1grupog3.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.tarea1grupog3.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse

class FragmentRestaurantes : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restaurantes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el cliente de ubicación y Places
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())

        // Obtener el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Verificar permisos y obtener la ubicación
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        // Habilitar la ubicación en el mapa
        mMap.isMyLocationEnabled = true

        // Obtener la ubicación actual y buscar restaurantes cercanos
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLocation = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                // Buscar restaurantes cercanos
                findNearbyRestaurants(userLocation)
            }
        }
    }

    // Función para buscar restaurantes cercanos utilizando la API de Places
    @SuppressLint("MissingPermission")
    private fun findNearbyRestaurants(location: LatLng) {
        // Crear una solicitud para encontrar lugares cercanos
        val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES)
        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        placesClient.findCurrentPlace(request).addOnSuccessListener { response: FindCurrentPlaceResponse ->
            val restaurantes = response.placeLikelihoods.filter { placeLikelihood ->
                placeLikelihood.place.types?.contains(Place.Type.RESTAURANT) == true
            }

            for (placeLikelihood in restaurantes) {
                val placeLatLng = placeLikelihood.place.latLng
                val placeName = placeLikelihood.place.name

                // Si es un lugar válido y tiene coordenadas
                if (placeLatLng != null && placeName != null) {
                    // Añadir un marcador para el restaurante
                    mMap.addMarker(
                        MarkerOptions().position(placeLatLng).title(placeName)
                    )
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Error al encontrar lugares: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
