package com.sigfred.kitchwa_app

import android.media.AudioAttributes
import android.media.MediaPlayer
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

    private lateinit var etInput: EditText
    private lateinit var btnTranslate: Button
    private lateinit var tvTranslation: TextView
    private lateinit var btnWordList: Button
    private lateinit var rvWordList: RecyclerView
    private lateinit var btnEscuchar: Button

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        etInput = findViewById(R.id.etInput)
        btnTranslate = findViewById(R.id.btnTranslate)
        tvTranslation = findViewById(R.id.tvTranslation)
        btnWordList = findViewById(R.id.btnWordList)
        rvWordList = findViewById(R.id.rvWordList)
        btnEscuchar = findViewById(R.id.textView6)

        rvWordList.layoutManager = LinearLayoutManager(this)
        rvWordList.visibility = View.GONE

        btnTranslate.setOnClickListener { buscarTraduccion() }
        btnWordList.setOnClickListener { toggleListaPalabras() }
        btnEscuchar.setOnClickListener { reproducirAudio() }
    }

    private fun buscarTraduccion() {
        val palabra = etInput.text.toString().trim().lowercase()
        if (palabra.isEmpty()) {
            etInput.error = "Ingrese una palabra"
            return
        }

        db.collection(coleccion).document(palabra).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val traducciones = document.get("español") as? List<*>
                    if (!traducciones.isNullOrEmpty()) {
                        tvTranslation.text = "Español: ${traducciones.joinToString(", ")}"
                    } else {
                        tvTranslation.text = "Traducción no encontrada"
                    }
                } else {
                    db.collection(coleccion).get()
                        .addOnSuccessListener { documents ->
                            val resultados = mutableListOf<String>()
                            for (doc in documents) {
                                val listaEsp = doc.get("español") as? List<*>
                                if (listaEsp?.map { it.toString().lowercase() }?.contains(palabra) == true) {
                                    doc.getString("kichwa")?.let {
                                        resultados.add("Kichwa: $it")
                                    }
                                }
                            }
                            tvTranslation.text = if (resultados.isNotEmpty()) resultados.joinToString("\n") else "Traducción no encontrada"
                        }
                        .addOnFailureListener { manejarError(it) }
                }
            }
            .addOnFailureListener { manejarError(it) }
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
        db.collection(coleccion).get()
            .addOnSuccessListener { result ->
                val palabras = result.map { doc ->
                    Palabra(
                        kichwa = doc.getString("kichwa") ?: "",
                        español = doc.get("español") as? List<String> ?: emptyList(),
                        audio_url = doc.getString("audio_url") ?: ""
                    )
                }
                rvWordList.adapter = PalabraAdapter(palabras)
            }
            .addOnFailureListener { exception ->
                Log.w("LISTA", "Error cargando palabras:", exception)
            }
    }

    private fun reproducirAudio() {
        val palabra = etInput.text.toString().trim().lowercase()
        if (palabra.isEmpty()) {
            etInput.error = "Ingrese una palabra"
            return
        }

        db.collection(coleccion).document(palabra).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val audioUrl = document.getString("audio_url")
                    if (!audioUrl.isNullOrEmpty()) {
                        reproducirDesdeUrl(audioUrl)
                    } else {
                        tvTranslation.text = "No hay audio disponible"
                    }
                } else {
                    tvTranslation.text = "No se encontró audio"
                }
            }
            .addOnFailureListener { manejarError(it) }
    }

    private fun reproducirDesdeUrl(url: String) {
        mediaPlayer?.release()
        mediaPlayer = null

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setOnPreparedListener { mp ->
                    mp.start()
                    tvTranslation.text = "Reproduciendo audio..."
                }
                setOnCompletionListener {
                    tvTranslation.text = "Audio completado"
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AUDIO", "Error: what=$what, extra=$extra")
                    tvTranslation.text = "Error al reproducir audio"
                    false
                }
                prepareAsync()
            } catch (e: Exception) {
                tvTranslation.text = "Error al reproducir audio"
                Log.e("AUDIO", "Error: ${e.message}")
            }
        }
    }

    private fun manejarError(exception: Exception) {
        Log.w("TRADUCCION", "Error:", exception)
        tvTranslation.text = "Error de conexión"
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    data class Palabra(
        val kichwa: String = "",
        val español: List<String> = emptyList(),
        val audio_url: String = ""
    )

    inner class PalabraAdapter(private val palabras: List<Palabra>) :
        RecyclerView.Adapter<PalabraAdapter.PalabraViewHolder>() {

        inner class PalabraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvKichwa: TextView = itemView.findViewById(R.id.tvKichwa)
            val tvSpanish: TextView = itemView.findViewById(R.id.tvSpanish)
            val tvAudio: TextView = itemView.findViewById(R.id.tvAudio)
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
            holder.tvAudio.text = if (palabra.audio_url.isNotEmpty()) "🔊" else "🔇"
        }

        override fun getItemCount(): Int = palabras.size
    }
}