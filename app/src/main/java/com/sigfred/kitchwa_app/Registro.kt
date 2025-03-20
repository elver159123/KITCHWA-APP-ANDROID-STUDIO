package com.sigfred.kitchwa_app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        auth = Firebase.auth

        // Obtener referencias de las vistas
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<AppCompatButton>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // Listener para el enlace "Acceder"
        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Listener para el botón de registro
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (!validarCampos(name, email, password, confirmPassword)) return@setOnClickListener

            registrarUsuario(name, email, password)
        }

        // Manejador de márgenes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validarCampos(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                findViewById<EditText>(R.id.etName).error = "Nombre requerido"
                false
            }
            email.isEmpty() -> {
                findViewById<EditText>(R.id.etEmail).error = "Email requerido"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                findViewById<EditText>(R.id.etEmail).error = "Email no válido"
                false
            }
            password.isEmpty() -> {
                findViewById<EditText>(R.id.etPassword).error = "Contraseña requerida"
                false
            }
            password.length < 6 -> {
                findViewById<EditText>(R.id.etPassword).error = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            password != confirmPassword -> {
                findViewById<EditText>(R.id.etConfirmPassword).error = "Las contraseñas no coinciden"
                false
            }
            else -> true
        }
    }

    private fun registrarUsuario(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    actualizarPerfilUsuario(name)
                } else {
                    mostrarErrorRegistro(task.exception?.message)
                }
            }
    }

    private fun actualizarPerfilUsuario(name: String) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                redirigirAUsuarioActivity()
            } else {
                Toast.makeText(
                    this,
                    "Error al actualizar el perfil: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun redirigirAUsuarioActivity() {
        Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, Usuario::class.java))
        finish()
    }

    private fun mostrarErrorRegistro(mensajeError: String?) {
        Toast.makeText(
            this,
            "Error en el registro: ${mensajeError ?: "Error desconocido"}",
            Toast.LENGTH_LONG
        ).show()
    }
}
