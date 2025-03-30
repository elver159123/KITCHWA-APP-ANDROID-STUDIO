package com.sigfred.kitchwa_app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sigfred.kitchwa_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupUI()
    }

    private fun setupUI() {
        binding.btnInciarSesion.setOnClickListener {
            loginUser()
        }

        binding.tvNoCuenta.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }

        binding.button5.setOnClickListener {
            Toast.makeText(this, "Inicio con Facebook", Toast.LENGTH_SHORT).show()
        }

        // Implementación de Google
        binding.button4.setOnClickListener {
            iniciarSesionGoogle()
        }
    }

    private fun iniciarSesionGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                autenticarConFirebase(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Error en inicio de Google: ", e)
                Toast.makeText(this, "Error en autenticación con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun autenticarConFirebase(idToken: String) {
        showLoading(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    navigateToUsuario()
                    Toast.makeText(this, "Autenticación con Google exitosa", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("GoogleAuth", "Error de autenticación: ", task.exception)
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Métodos existentes (NO MODIFICAR)
    private fun loginUser() {
        val email = binding.editTextText.text.toString().trim()
        val password = binding.editTextText2.text.toString().trim()

        if (!validateInputs(email, password)) return

        showLoading(true)

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            showLoading(false)
            if (task.isSuccessful) {
                navigateToUsuario()
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            } else {
                showError("Error en la autenticación: ${task.exception?.message}")
            }
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

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 9001
    }
}