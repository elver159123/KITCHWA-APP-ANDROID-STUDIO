package com.sigfred.kitchwa_app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sigfred.kitchwa_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Muestra el splash screen
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Configura el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mantén el splash screen visible hasta que se complete el setup inicial
        splashScreen.setKeepOnScreenCondition { false }

        // Configura los listeners para los botones
        binding.button.setOnClickListener {
            handleLogin()
        }

        binding.button5.setOnClickListener {
            // Simula inicio de sesión con Facebook
            Toast.makeText(this, "Iniciar sesión con Facebook", Toast.LENGTH_SHORT).show()
        }

        binding.button4.setOnClickListener {
            // Simula inicio de sesión con Google
            Toast.makeText(this, "Iniciar sesión con Google", Toast.LENGTH_SHORT).show()
        }

        binding.textView2.setOnClickListener {
            // Navega a la pantalla de registro
            Toast.makeText(this, "Ir a la pantalla de registro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLogin() {
        val email = binding.editTextText.text.toString()
        val password = binding.editTextText2.text.toString()

        if (isValidInput(email, password)) {
            // Simula un inicio de sesión exitoso
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
        } else {
            // Muestra un mensaje de error
            Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isNotEmpty()
    }
}
