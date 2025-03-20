package com.sigfred.kitchwa_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Usuario : AppCompatActivity() {

    private val db = Firebase.firestore
    private val coleccion = "traduccion"

    // Componentes de UI
    private lateinit var etInput: EditText
    private lateinit var btnTranslate: Button
    private lateinit var tvTranslation: TextView
    private lateinit var btnWordList: Button
    private lateinit var rvWordList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        // Inicializar componentes
        etInput = findViewById(R.id.etInput)
        btnTranslate = findViewById(R.id.btnTranslate)
        tvTranslation = findViewById(R.id.tvTranslation)
        btnWordList = findViewById(R.id.btnWordList)
        rvWordList = findViewById(R.id.rvWordList)

        // Configurar RecyclerView
        rvWordList.layoutManager = LinearLayoutManager(this)
        rvWordList.visibility = View.GONE

        // Listeners
        btnTranslate.setOnClickListener { buscarTraduccion() }
        btnWordList.setOnClickListener { toggleListaPalabras() }
    }

    private fun buscarTraduccion() {
        val palabra = etInput.text.toString().trim().lowercase()
        if (palabra.isEmpty()) {
            etInput.error = "Ingrese una palabra"
            return
        }

        // Buscar en Kichwa
        db.collection(coleccion)
            .whereEqualTo("kichwa", palabra)
            .get()
            .addOnSuccessListener { documents ->
                val resultados = mutableListOf<String>()
                for (document in documents) {
                    val traducciones = document.get("español") as? List<*>
                    traducciones?.forEach {
                        if (it is String) resultados.add("Español: ${it.capitalize()}")
                    }
                }

                // Buscar en Español
                db.collection(coleccion)
                    .whereArrayContains("español", palabra)
                    .get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            doc.getString("kichwa")?.let {
                                resultados.add("Kichwa: ${it.capitalize()}")
                            }
                        }

                        mostrarResultados(resultados)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("TRADUCCION", "Error:", exception)
                tvTranslation.text = "Error de conexión"
            }
    }

    private fun mostrarResultados(resultados: List<String>) {
        tvTranslation.text = if (resultados.isNotEmpty()) {
            resultados.distinct().joinToString("\n")
        } else {
            "Traducción no encontrada"
        }
    }

    private fun toggleListaPalabras() {
        if (rvWordList.visibility == View.VISIBLE) {
            rvWordList.visibility = View.GONE
        } else {
            cargarListaDesdeFirebase()
            rvWordList.visibility = View.VISIBLE
        }
    }

    private fun cargarListaDesdeFirebase() {
        db.collection(coleccion)
            .get()
            .addOnSuccessListener { result ->
                val palabras = result.map { doc ->
                    Palabra(
                        kichwa = doc.getString("kichwa") ?: "",
                        español = doc.get("español") as? List<String> ?: emptyList()
                    )
                }
                rvWordList.adapter = PalabraAdapter(palabras)
            }
            .addOnFailureListener { exception ->
                Log.w("LISTA", "Error cargando palabras:", exception)
            }
    }

    // Data class para las palabras
    data class Palabra(
        val kichwa: String = "",
        val español: List<String> = emptyList()
    )

    // Adaptador para el RecyclerView
    inner class PalabraAdapter(private val palabras: List<Palabra>) :
        RecyclerView.Adapter<PalabraAdapter.PalabraViewHolder>() {

        inner class PalabraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvKichwa: TextView = itemView.findViewById(R.id.tvKichwa)
            val tvSpanish: TextView = itemView.findViewById(R.id.tvSpanish)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PalabraViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_word, parent, false)
            return PalabraViewHolder(view)
        }

        override fun onBindViewHolder(holder: PalabraViewHolder, position: Int) {
            val palabra = palabras[position]
            holder.tvKichwa.text = palabra.kichwa
            holder.tvSpanish.text = palabra.español.joinToString(", ")
        }

        override fun getItemCount(): Int = palabras.size
    }
}