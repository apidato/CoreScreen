package com.ilmer.shellscreen

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class ConfigActivity : AppCompatActivity() {

    private lateinit var inputColorFondo: EditText
    private lateinit var inputColorTexto: EditText
    private lateinit var inputColorWidget: EditText
    private lateinit var inputTamanoFuente: EditText

    private lateinit var radioMono: RadioButton
    private lateinit var radioSans: RadioButton
    private lateinit var radioSerif: RadioButton

    private lateinit var radioRelojDigital: RadioButton
    private lateinit var radioRelojAnalogico: RadioButton
    private lateinit var checkMostrarNombre: CheckBox
    private lateinit var inputNombreDuenio: EditText
    private lateinit var checkMantenerDespierto: CheckBox

    // Vistas para el selector de idioma
    private lateinit var radioIdiomaSistema: RadioButton
    private lateinit var radioIdiomaEs: RadioButton
    private lateinit var radioIdiomaEn: RadioButton
    private lateinit var radioIdiomaPt: RadioButton
    private lateinit var radioIdiomaDe: RadioButton
    private lateinit var radioIdiomaFr: RadioButton

    private lateinit var checkBuscarEnTodas: CheckBox
    private lateinit var contenedorFavoritas: LinearLayout
    private lateinit var contenedorAppsPantalla: LinearLayout

    private val listaNombresApps = mutableListOf<String>()
    private val checksFavoritas = mutableListOf<CheckBox>()
    private val checksPantalla = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        setContentView(R.layout.activity_config)

        vincularVistas()
        configurarAcordeones()
        configurarPaletaRapida()

        cargarListaDeAplicacionesInstaladas()
        cargarDatosGuardados()

        findViewById<TextView>(R.id.btnRegresarTop).setOnClickListener { finish() }
        findViewById<TextView>(R.id.btnSeleccionarLauncher).setOnClickListener { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
        findViewById<TextView>(R.id.btnGuardar).setOnClickListener { guardarConfiguracion() }
    }

    private fun vincularVistas() {
        inputColorFondo = findViewById(R.id.inputColorFondo)
        inputColorTexto = findViewById(R.id.inputColorTexto)
        inputColorWidget = findViewById(R.id.inputColorWidget)
        inputTamanoFuente = findViewById(R.id.inputTamanoFuente)

        radioMono = findViewById(R.id.radioMono)
        radioSans = findViewById(R.id.radioSans)
        radioSerif = findViewById(R.id.radioSerif)

        radioRelojDigital = findViewById(R.id.radioRelojDigital)
        radioRelojAnalogico = findViewById(R.id.radioRelojAnalogico)
        checkMostrarNombre = findViewById(R.id.checkMostrarNombre)
        inputNombreDuenio = findViewById(R.id.inputNombreDuenio)
        checkMantenerDespierto = findViewById(R.id.checkMantenerDespierto)

        // Vincular RadioButtons de Idioma
        radioIdiomaSistema = findViewById(R.id.radioIdiomaSistema)
        radioIdiomaEs = findViewById(R.id.radioIdiomaEs)
        radioIdiomaEn = findViewById(R.id.radioIdiomaEn)
        radioIdiomaPt = findViewById(R.id.radioIdiomaPt)
        radioIdiomaDe = findViewById(R.id.radioIdiomaDe)
        radioIdiomaFr = findViewById(R.id.radioIdiomaFr)

        checkBuscarEnTodas = findViewById(R.id.checkBuscarEnTodas)
        contenedorFavoritas = findViewById(R.id.contenedorFavoritas)
        contenedorAppsPantalla = findViewById(R.id.contenedorAppsPantalla)
    }

    private fun configurarAcordeones() {
        val secciones = listOf(
            Pair(findViewById<TextView>(R.id.headerColores), findViewById<LinearLayout>(R.id.contentColores)),
            Pair(findViewById<TextView>(R.id.headerFavoritas), findViewById<LinearLayout>(R.id.contentFavoritas)),
            Pair(findViewById<TextView>(R.id.headerVisibles), findViewById<LinearLayout>(R.id.contentVisibles)),
            Pair(findViewById<TextView>(R.id.headerFuentes), findViewById<LinearLayout>(R.id.contentFuentes)),
            Pair(findViewById<TextView>(R.id.headerBloqueo), findViewById<LinearLayout>(R.id.contentBloqueo)),
            Pair(findViewById<TextView>(R.id.headerIdioma), findViewById<LinearLayout>(R.id.contentIdioma))
        )

        for ((header, content) in secciones) {
            header.setOnClickListener {
                content.visibility = if (content.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }
    }

    private fun configurarPaletaRapida() {
        val asginarColor = { hex: String ->
            inputColorTexto.setText(hex)
            inputColorWidget.setText(hex)
        }
        findViewById<View>(R.id.cVerde).setOnClickListener { asginarColor("#00FF00") }
        findViewById<View>(R.id.cAmarillo).setOnClickListener { asginarColor("#FFFF00") }
        findViewById<View>(R.id.cAzul).setOnClickListener { asginarColor("#00FFFF") }
        findViewById<View>(R.id.cRosa).setOnClickListener { asginarColor("#FF00FF") }
        findViewById<View>(R.id.cBlanco).setOnClickListener { asginarColor("#FFFFFF") }
    }

    private fun cargarListaDeAplicacionesInstaladas() {
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        for (info in pm.queryIntentActivities(mainIntent, 0)) {
            listaNombresApps.add(info.loadLabel(pm).toString())
        }
        listaNombresApps.sortWith(String.CASE_INSENSITIVE_ORDER)
    }

    private fun cargarDatosGuardados() {
        val prefs = getSharedPreferences("ShellScreenPrefs", Context.MODE_PRIVATE)

        inputColorFondo.setText(prefs.getString("colorFondo", "#000000"))
        inputColorTexto.setText(prefs.getString("colorTexto", "#00FF00"))
        inputColorWidget.setText(prefs.getString("colorWidget", "#FFFFFF"))

        val tamanoActual = prefs.getFloat("tamanoFuente", 14f)
        inputTamanoFuente.setText(tamanoActual.toInt().toString())

        when (prefs.getString("tipoFuente", "sans")) {
            "monospace" -> radioMono.isChecked = true
            "serif" -> radioSerif.isChecked = true
            else -> radioSans.isChecked = true
        }

        if (prefs.getString("tipoReloj", "digital") == "analogico") {
            radioRelojAnalogico.isChecked = true
        } else {
            radioRelojDigital.isChecked = true
        }
        checkMostrarNombre.isChecked = prefs.getBoolean("mostrarNombre", false)
        inputNombreDuenio.setText(prefs.getString("nombreDuenio", ""))
        checkMantenerDespierto.isChecked = prefs.getBoolean("mantenerDespierto", false)

        // Cargar selección de idioma guardada
        when (prefs.getString("appIdioma", "sistema")) {
            "es" -> radioIdiomaEs.isChecked = true
            "en" -> radioIdiomaEn.isChecked = true
            "pt" -> radioIdiomaPt.isChecked = true
            "de" -> radioIdiomaDe.isChecked = true
            "fr" -> radioIdiomaFr.isChecked = true
            else -> radioIdiomaSistema.isChecked = true
        }

        checkBuscarEnTodas.isChecked = prefs.getBoolean("buscarEnTodas", false)

        val favoritasGuardadas = prefs.getStringSet("appsFavoritas", setOf()) ?: setOf()
        val pantallaGuardadas = prefs.getStringSet("appsPantalla", setOf()) ?: setOf()

        for (nombreApp in listaNombresApps) {
            val cbFav = CheckBox(this).apply { text = nombreApp; setTextColor(Color.WHITE); isChecked = favoritasGuardadas.contains(nombreApp) }
            cbFav.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && checksFavoritas.count { it.isChecked } > 3) {
                    cbFav.isChecked = false
                    Toast.makeText(this@ConfigActivity, getString(R.string.cfg_toast_max_favoritas), Toast.LENGTH_SHORT).show()
                }
            }
            contenedorFavoritas.addView(cbFav)
            checksFavoritas.add(cbFav)

            val cbPantalla = CheckBox(this).apply { text = nombreApp; setTextColor(Color.WHITE); isChecked = pantallaGuardadas.contains(nombreApp) }
            contenedorAppsPantalla.addView(cbPantalla)
            checksPantalla.add(cbPantalla)
        }
    }

    private fun guardarConfiguracion() {
        val cFondo = inputColorFondo.text.toString().uppercase()
        val cTexto = inputColorTexto.text.toString().uppercase()
        val cWidget = inputColorWidget.text.toString().uppercase()

        if (!validarColor(cFondo) || !validarColor(cTexto) || !validarColor(cWidget)) {
            Toast.makeText(this, getString(R.string.cfg_toast_error_color), Toast.LENGTH_LONG).show()
            return
        }

        var tamano = inputTamanoFuente.text.toString().toFloatOrNull() ?: 14f
        if (tamano < 10f) tamano = 10f
        if (tamano > 24f) tamano = 24f

        val prefs = getSharedPreferences("ShellScreenPrefs", Context.MODE_PRIVATE).edit()

        prefs.putString("colorFondo", cFondo)
        prefs.putString("colorTexto", cTexto)
        prefs.putString("colorWidget", cWidget)
        prefs.putFloat("tamanoFuente", tamano)

        val fuente = if (radioMono.isChecked) "monospace" else if (radioSerif.isChecked) "serif" else "sans"
        prefs.putString("tipoFuente", fuente)

        val tipoReloj = if (radioRelojAnalogico.isChecked) "analogico" else "digital"
        prefs.putString("tipoReloj", tipoReloj)
        prefs.putBoolean("mostrarNombre", checkMostrarNombre.isChecked)
        prefs.putString("nombreDuenio", inputNombreDuenio.text.toString())
        prefs.putBoolean("mantenerDespierto", checkMantenerDespierto.isChecked)

        // Guardar y aplicar el idioma seleccionado
        val codigoIdioma = when {
            radioIdiomaEs.isChecked -> "es"
            radioIdiomaEn.isChecked -> "en"
            radioIdiomaPt.isChecked -> "pt"
            radioIdiomaDe.isChecked -> "de"
            radioIdiomaFr.isChecked -> "fr"
            else -> "sistema"
        }
        prefs.putString("appIdioma", codigoIdioma)

        if (codigoIdioma == "sistema") {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(codigoIdioma))
        }

        prefs.putBoolean("buscarEnTodas", checkBuscarEnTodas.isChecked)

        val favs = checksFavoritas.filter { it.isChecked }.map { it.text.toString() }.toSet()
        prefs.putStringSet("appsFavoritas", favs)

        val pantalla = checksPantalla.filter { it.isChecked }.map { it.text.toString() }.toSet()
        prefs.putStringSet("appsPantalla", pantalla)

        prefs.apply()
        Toast.makeText(this, getString(R.string.cfg_toast_aplicado), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun validarColor(colorStr: String): Boolean {
        return try { Color.parseColor(colorStr); true } catch (e: Exception) { false }
    }
}