package com.sigfred.kitchwa_app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.sigfred.kitchwa_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupUI()
    }

    private fun setupUI() {
        // Botón de inicio de sesión principal
        binding.button.setOnClickListener {
            loginUser()
        }

        // Texto para ir al registro
        binding.textView2.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }

        // Botón de Facebook
        binding.button5.setOnClickListener {
            Toast.makeText(this, "Inicio con Facebook", Toast.LENGTH_SHORT).show()
            // Implementar lógica de Facebook aquí
        }

        // Botón de Google
        binding.button4.setOnClickListener {
            Toast.makeText(this, "Inicio con Google", Toast.LENGTH_SHORT).show()
            // Implementar lógica de Google aquí
        }
    }

    private fun loginUser() {
        val email = binding.editTextText.text.toString().trim()
        val password = binding.editTextText2.text.toString().trim()

        if (!validateInputs(email, password)) return

        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                handleLoginResult(task)
            }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showError("Ingrese su correo")
                false
            }
            TextUtils.isEmpty(password) -> {
                showError("Ingrese su contraseña")
                false
            }
            else -> true
        }
    }

    private fun handleLoginResult(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            navigateToUsuario()
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
        } else {
            showError("Error en la autenticación: ${task.exception?.message}")
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToUsuario() {
        val intent = Intent(this, Usuario::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}