package com.sigfred.kitchwa_app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputEditText
import com.sigfred.kitchwa_app.R

class Registro : AppCompatActivity() {
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonRegister: Button
    private lateinit var textViewLogin: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        mAuth = FirebaseAuth.getInstance()
        editTextEmail = findViewById(R.id.etEmail)
        editTextPassword = findViewById(R.id.etPassword)
        buttonRegister = findViewById(R.id.btnRegister)
        textViewLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        // Botón para registrar un nuevo usuario
        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Ingrese su correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                        // Devolver email al Login
                        val resultIntent = Intent()
                        resultIntent.putExtra("EMAIL", email)
                        setResult(RESULT_OK, resultIntent)
                        finish()  // Cerrar la actividad y volver a Login
                    } else {
                        Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Redirigir al usuario a la pantalla de login si ya tiene cuenta
        textViewLogin.setOnClickListener {
            finish() // Cierra la actividad actual y vuelve a Login
        }
    }
}