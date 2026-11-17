package com.ilmer.shellscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var rootLayout: LinearLayout
    private lateinit var btnIconConfig: TextView
    private lateinit var txtDispositivo: TextView
    private lateinit var txtBateria: TextView
    private lateinit var txtRelojWidget: TextView
    private lateinit var txtFechaWidget: TextView
    private lateinit var layoutFavoritos: LinearLayout
    private lateinit var layoutListaApps: LinearLayout
    private lateinit var inputTerminal: EditText

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnableReloj: Runnable
    private var screenReceiver: BroadcastReceiver? = null

    private var textoIngresado = ""
    private var modeloLimpio = ""

    private var colorFondo = "#000000"
    private var colorTexto = "#00FF00"
    private var colorWidget = "#FFFFFF"
    private var buscarEnTodas = false
    private var appsFavoritas = setOf<String>()
    private var appsPantalla = setOf<String>()
    private var todasLasAppsInstaladas = mapOf<String, String>()

    private var tamanoFuenteListas = 14f
    private var familiaFuente = Typeface.SANS_SERIF

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level != -1 && scale != -1) {
                    txtBateria.text = "${(level * 100) / scale}%"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout)
        btnIconConfig = findViewById(R.id.btnIconConfig)
        txtDispositivo = findViewById(R.id.txtDispositivo)
        txtBateria = findViewById(R.id.txtBateria)
        txtRelojWidget = findViewById(R.id.txtRelojWidget)
        txtFechaWidget = findViewById(R.id.txtFechaWidget)
        layoutFavoritos = findViewById(R.id.layoutFavoritos)
        layoutListaApps = findViewById(R.id.layoutListaApps)
        inputTerminal = findViewById(R.id.inputTerminal)

        modeloLimpio = Build.MODEL.replace(" ", "")
        txtDispositivo.text = modeloLimpio

        cargarAppsInstaladas()
        iniciarRelojEnVivo()
        configurarBuscador()
        registrarEscuchadorPantalla()

        btnIconConfig.setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        cargarConfiguraciones()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }

    private fun registrarEscuchadorPantalla() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                    context?.let { ctx ->
                        val intentLock = Intent(ctx, LockScreenActivity::class.java)
                        intentLock.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        ctx.startActivity(intentLock)
                    }
                }
            }
        }
        registerReceiver(screenReceiver, filter)
    }

    private fun cargarAppsInstaladas() {
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)

        val mapaTemporal = mutableMapOf<String, String>()
        for (info in resolveInfos) {
            mapaTemporal[info.loadLabel(pm).toString()] = info.activityInfo.packageName
        }
        todasLasAppsInstaladas = mapaTemporal.toSortedMap(String.CASE_INSENSITIVE_ORDER)
    }

    private fun abrirApp(nombreApp: String) {
        val paquete = todasLasAppsInstaladas[nombreApp]
        if (paquete != null) {
            val intent = packageManager.getLaunchIntentForPackage(paquete)
            if (intent != null) {
                startActivity(intent)
                inputTerminal.setText("")
            } else {
                Toast.makeText(this, getString(R.string.msg_error_abrir), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarConfiguraciones() {
        val prefs = getSharedPreferences("ShellScreenPrefs", Context.MODE_PRIVATE)
        colorFondo = prefs.getString("colorFondo", "#000000") ?: "#000000"
        colorTexto = prefs.getString("colorTexto", "#00FF00") ?: "#00FF00"
        colorWidget = prefs.getString("colorWidget", "#FFFFFF") ?: "#FFFFFF"
        buscarEnTodas = prefs.getBoolean("buscarEnTodas", false)
        appsFavoritas = prefs.getStringSet("appsFavoritas", setOf()) ?: setOf()
        appsPantalla = prefs.getStringSet("appsPantalla", setOf()) ?: setOf()

        tamanoFuenteListas = prefs.getFloat("tamanoFuente", 14f)
        familiaFuente = when(prefs.getString("tipoFuente", "sans")) {
            "monospace" -> Typeface.MONOSPACE
            "serif" -> Typeface.SERIF
            else -> Typeface.SANS_SERIF
        }

        aplicarColoresUI()
        actualizarFavoritos()
        actualizarListaApps(textoIngresado)
    }

    private fun aplicarColoresUI() {
        try {
            rootLayout.setBackgroundColor(Color.parseColor(colorFondo))
            btnIconConfig.setTextColor(Color.parseColor(colorWidget))
            txtDispositivo.setTextColor(Color.parseColor(colorWidget))
            txtBateria.setTextColor(Color.parseColor(colorWidget))
            txtRelojWidget.setTextColor(Color.parseColor(colorWidget))

            // Aplicar estilo dinámico al contenedor del buscador usando el color de fondo configurado
            val contenedorBuscador = inputTerminal.parent as? LinearLayout
            if (contenedorBuscador != null) {
                val colorBase = Color.parseColor(colorTexto)
                val colorFondoDeseado = Color.parseColor(colorFondo)
                val colorBordeTenue = Color.argb(80, Color.red(colorBase), Color.green(colorBase), Color.blue(colorBase))

                val drawableBuscador = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 24f
                    setColor(colorFondoDeseado)
                    setStroke(2, colorBordeTenue)
                }
                contenedorBuscador.background = drawableBuscador
            }
        } catch (e: Exception) {}
    }

    private fun crearBurbujaApp(nombre: String): TextView {
        val tv = TextView(this)
        tv.text = nombre
        tv.textSize = tamanoFuenteListas
        tv.typeface = familiaFuente
        tv.gravity = Gravity.CENTER
        tv.setPadding(24, 16, 24, 16)

        try {
            val colorBase = Color.parseColor(colorTexto)
            val colorFondoDeseado = Color.parseColor(colorFondo)
            val colorBordeTenue = Color.argb(80, Color.red(colorBase), Color.green(colorBase), Color.blue(colorBase))

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 24f
                setColor(colorFondoDeseado)
                setStroke(2, colorBordeTenue)
            }
            tv.background = drawable
            tv.setTextColor(colorBase)
        } catch (e: Exception) {
            tv.setBackgroundResource(R.drawable.burbuja_app)
        }

        tv.isClickable = true
        tv.isFocusable = true
        tv.setOnClickListener { abrirApp(nombre) }
        return tv
    }

    private fun actualizarFavoritos() {
        layoutFavoritos.removeAllViews()
        if (appsFavoritas.isEmpty()) {
            val tv = TextView(this).apply {
                text = getString(R.string.msg_sin_favoritos)
                textSize = 13f
                setTextColor(Color.parseColor("#666666"))
                gravity = Gravity.CENTER
            }
            layoutFavoritos.addView(tv)
        } else {
            for (app in appsFavoritas.take(3)) {
                val burbuja = crearBurbujaApp(app)
                val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(8, 0, 8, 0)
                }
                burbuja.layoutParams = params
                layoutFavoritos.addView(burbuja)
            }
        }
    }

    private fun configurarBuscador() {
        inputTerminal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                textoIngresado = s.toString()
                actualizarListaApps(textoIngresado)
            }
        })
    }

    private fun actualizarListaApps(filtro: String) {
        layoutListaApps.removeAllViews()

        val listaBase = if (buscarEnTodas) todasLasAppsInstaladas.keys else appsPantalla
        val filtradas = listaBase.filter { it.contains(filtro, ignoreCase = true) }.sorted()

        if (listaBase.isEmpty() && !buscarEnTodas) {
            val msg = TextView(this).apply {
                text = getString(R.string.msg_config_apps)
                setTextColor(Color.parseColor("#888888"))
                textSize = 14f
                setPadding(0, 16, 0, 16)
            }
            layoutListaApps.addView(msg)
        } else if (filtradas.isEmpty()) {
            val msg = TextView(this).apply {
                text = getString(R.string.msg_sin_coincidencia)
                setTextColor(Color.parseColor("#888888"))
                textSize = 14f
                setPadding(0, 16, 0, 16)
            }
            layoutListaApps.addView(msg)
        } else {
            var filaActual = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    bottomMargin = 12
                }
            }

            for ((index, app) in filtradas.withIndex()) {
                val burbuja = crearBurbujaApp(app)
                val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(6, 0, 6, 0)
                }
                burbuja.layoutParams = params
                filaActual.addView(burbuja)

                if ((index + 1) % 2 == 0 || index == filtradas.size - 1) {
                    layoutListaApps.addView(filaActual)
                    filaActual = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            bottomMargin = 12
                        }
                    }
                }
            }
        }
    }

    private fun iniciarRelojEnVivo() {
        runnableReloj = object : Runnable {
            override fun run() {
                val ahora = Date()
                txtRelojWidget.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(ahora)

                // Obtener el idioma guardado en SharedPreferences para adaptar la fecha de forma dinámica
                val prefs = getSharedPreferences("ShellScreenPrefs", Context.MODE_PRIVATE)
                val idiomaActual = prefs.getString("appIdioma", "sistema") ?: "sistema"

                val localeActual = if (idiomaActual == "sistema") {
                    Locale.getDefault()
                } else {
                    Locale(idiomaActual)
                }

                val formatoFecha = SimpleDateFormat("EEEE, d 'de' MMMM", localeActual)
                txtFechaWidget.text = formatoFecha.format(ahora).replaceFirstChar { it.uppercase() }

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnableReloj)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnableReloj)
        screenReceiver?.let {
            try { unregisterReceiver(it) } catch (e: Exception) {}
        }
    }
}