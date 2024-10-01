package com.example.tarea1grupog3.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarea1grupog3.R
import com.example.tarea1grupog3.databases.DatabaseHelper
import com.example.tarea1grupog3.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var editcorreo: EditText
    private lateinit var editpasword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editcorreo = findViewById(R.id.inputEmail)
        editpasword = findViewById(R.id.inputPassword)

        binding.btnGoToMenu.setOnClickListener {
            val correo = editcorreo.text.toString()
            val password = editpasword.text.toString()

            if(TextUtils.isEmpty(correo) || TextUtils.isEmpty(password)){
                Toast.makeText(this,"Rellene todos los campos ", Toast.LENGTH_SHORT).show()
            }else{

                val databaseHelper = DatabaseHelper(this)
                val isUserValid = databaseHelper.validateUser(correo, password)

                if(isUserValid){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,"Usuario o contraseña incorrecto ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}