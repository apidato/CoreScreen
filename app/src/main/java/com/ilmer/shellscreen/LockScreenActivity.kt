package com.ilmer.shellscreen

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LockScreenActivity : AppCompatActivity() {

    private lateinit var rootLockLayout: LinearLayout
    private lateinit var txtNombreDuenio: TextView
    private lateinit var contenedorReloj: FrameLayout
    private lateinit var txtRelojBloqueo: TextView

    private var analogClockView: MinimalAnalogClockView? = null

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnableReloj: Runnable

    private var tipoReloj = "digital"
    private var mostrarNombre = false
    private var nombreDuenio = ""
    private var colorFondo = "#000000"
    private var colorTexto = "#00FF00"
    private var colorWidget = "#FFFFFF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )

        setContentView(R.layout.activity_lock_screen)

        rootLockLayout = findViewById(R.id.rootLockLayout)
        txtNombreDuenio = findViewById(R.id.txtNombreDuenio)
        contenedorReloj = findViewById(R.id.contenedorReloj)
        txtRelojBloqueo = findViewById(R.id.txtRelojBloqueo)

        cargarPreferencias()
        configurarTipoReloj()
        iniciarRelojBloqueo()

        rootLockLayout.setOnClickListener {
            finish()
        }
    }

    private fun cargarPreferencias() {
        val prefs = getSharedPreferences("ShellScreenPrefs", Context.MODE_PRIVATE)
        colorFondo = prefs.getString("colorFondo", "#000000") ?: "#000000"
        colorTexto = prefs.getString("colorTexto", "#00FF00") ?: "#00FF00"
        colorWidget = prefs.getString("colorWidget", "#FFFFFF") ?: "#FFFFFF"

        tipoReloj = prefs.getString("tipoReloj", "digital") ?: "digital"
        mostrarNombre = prefs.getBoolean("mostrarNombre", false)
        nombreDuenio = prefs.getString("nombreDuenio", "") ?: ""

        val mantenerDespierto = prefs.getBoolean("mantenerDespierto", false)
        if (mantenerDespierto) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        try {
            rootLockLayout.setBackgroundColor(Color.parseColor(colorFondo))
        } catch (e: Exception) {}

        if (mostrarNombre) {
            actualizarSaludoDinamico()
            try { txtNombreDuenio.setTextColor(Color.parseColor(colorWidget)) } catch (e: Exception) {}
            txtNombreDuenio.visibility = View.VISIBLE
        } else {
            txtNombreDuenio.visibility = View.GONE
        }
    }

    private fun actualizarSaludoDinamico() {
        val horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val saludoBase = when (horaActual) {
            in 6..11 -> getString(R.string.saludo_manana)
            in 12..18 -> getString(R.string.saludo_tarde)
            else -> getString(R.string.saludo_noche)
        }

        if (nombreDuenio.isNotBlank()) {
            txtNombreDuenio.text = "$saludoBase $nombreDuenio,"
        } else {
            txtNombreDuenio.text = "$saludoBase,"
        }
    }

    private fun configurarTipoReloj() {
        if (tipoReloj == "analogico") {
            txtRelojBloqueo.visibility = View.GONE
            val clock = MinimalAnalogClockView(this)
            try {
                clock.setClockColor(Color.parseColor(colorTexto))
            } catch (e: Exception) {}
            analogClockView = clock
            contenedorReloj.addView(analogClockView)
        } else {
            txtRelojBloqueo.visibility = View.VISIBLE
            try {
                txtRelojBloqueo.setTextColor(Color.parseColor(colorTexto))
            } catch (e: Exception) {}
        }
    }

    private fun iniciarRelojBloqueo() {
        runnableReloj = object : Runnable {
            override fun run() {
                val ahora = Date()

                if (tipoReloj == "analogico") {
                    analogClockView?.invalidate()
                } else {
                    val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    txtRelojBloqueo.text = formatoHora.format(ahora)
                }

                if (mostrarNombre) {
                    actualizarSaludoDinamico()
                }

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnableReloj)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnableReloj)
    }
}