package com.sigfred.kitchwa_app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputEditText
import com.sigfred.kitchwa_app.R
import com.sigfred.kitchwa_app.databinding.ActivityRegistroBinding
import com.sigfred.kitchwa_app.model.User

class Registro : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()


        // Botón para registrar un nuevo usuario
        binding.btnRegister.setOnClickListener {

            crearCuenta()

        }


    }

    private fun crearCuenta() {
        validarUsuario()?.let {
            binding.progressBar.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(it.email, it.password)
                .addOnSuccessListener { task ->
                    binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        navegarNuevaPantalla(it.email)
                }.addOnFailureListener {
                    Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                    println("error al registrar "+it.message)
                }
        }
    }

    private fun navegarNuevaPantalla(email: String) {
        // Devolver email al Login
        val resultIntent = Intent()
        resultIntent.putExtra("EMAIL", email)
        setResult(RESULT_OK, resultIntent)
        finish()  // Cerrar la actividad y volver a Login
    }

    private fun validarUsuario(): User? {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val nombre = binding.etName.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Ingrese su correo", Toast.LENGTH_SHORT).show()
            return null
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show()
            return null
        } else if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_SHORT).show()
            return null
        } else {
            return User(nombre, email, password)
        }
    }
}