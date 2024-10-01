package com.example.tarea1grupog3.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.tarea1grupog3.R
import com.example.tarea1grupog3.databinding.ActivityMainBinding
import com.example.tarea1grupog3.fragments.FragmentUbicacionActual
import com.example.tarea1grupog3.fragments.FragmentRestaurantes
import com.example.tarea1grupog3.fragments.FragmentLugares

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Botones
        binding.btnFragment1.setOnClickListener {
            // Lógica para mostrar fragmento 1
            replaceFragment(FragmentUbicacionActual())
        }

        binding.btnFragment2.setOnClickListener {
            // Lógica para mostrar fragmento 2
            replaceFragment(FragmentLugares())
        }

        binding.btnFragment3.setOnClickListener {
            // Lógica para mostrar fragmento 3
            replaceFragment(FragmentRestaurantes())
        }
    }

    // Lógica para cambiar fragmentos
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}