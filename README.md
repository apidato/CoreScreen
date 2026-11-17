# CoreScreen

[ 🇬🇧 English | 🇪🇸 Español ]

---

## 🇬🇧 English

**CoreScreen** is a minimalist Android launcher focused on simplicity, zero visual noise, and a fluid user experience. Designed to deliver a clean, professional, and essential interface from the very first touch.

### ✨ Key Features
- **Dynamic Multi-Language Support:** Full runtime localization for English, Spanish, Portuguese, German, and French using `AppCompatDelegate` and `LocaleListCompat`. Users can choose their preferred language or follow the system default directly from the settings menu.
- **Minimalist Lock Screen:** Personalized dynamic greetings (morning, afternoon, evening), clean date formatting, and a choice between a digital or a smooth analog clock (`MinimalAnalogClockView`).
- **Custom Analog Clock (`MinimalAnalogClockView`):** Optimized graphic component built with custom `Canvas` rendering, featuring smooth hands and a subtle second hand.
- **Monochromatic & Clean Design:** High-contrast dark aesthetic with zero visual clutter, no signal bars, and no unnecessary footer branding.
- **Flexible Customization:** Built-in settings for color palettes (background, text/accent, widgets), custom list font sizes and font families (Monospace, Sans-serif, Serif), favorite apps pinning (up to 3), and an optional full-phone search mode.
- **Keep Screen Awake Option:** Prevent screen timeout directly from preferences when needed.

### 🛠️ Technologies
- **Language:** Kotlin
- **Platform:** Android (Activities, Intents, SharedPreferences, Custom Views with `Canvas`)
- **Architecture & UI:** AppCompat, Dynamic Runtime Locales, Immersive Sticky UI

### 🚀 Installation & Usage
1. Clone the repository:
   ```bash
   git clone [https://github.com/apidato/CoreScreen.git](https://github.com/apidato/CoreScreen.git)

Open the project in Android Studio.

    Build, run, and set it as your default home application (launcher) via Android settings.

🇪🇸 Español

CoreScreen es un launcher minimalista para Android enfocado en la simplicidad, la ausencia de ruido visual y una interfaz fluida. Diseñado para ofrecer una experiencia limpia, profesional y esencial desde el primer toque.
✨ Características Principales

    Soporte Multilenguaje Dinámico: Internacionalización completa en tiempo de ejecución para Español (por defecto), Inglés, Portugués, Alemán y Francés utilizando AppCompatDelegate y LocaleListCompat. Los usuarios pueden elegir su idioma preferido o seguir el predeterminado del sistema directamente desde los ajustes.

    Pantalla de Bloqueo Minimalista: Saludos dinámicos personalizados (mañana, tarde, noche), formato de fecha limpio y selector entre reloj digital o analógico de manecillas (MinimalAnalogClockView).

    Reloj Analógico Personalizado (MinimalAnalogClockView): Componente gráfico optimizado mediante renderizado en Canvas, con manecillas suaves y un segundero sutil.

    Diseño Limpio y Monocromático: Estética oscura de alto contraste sin barras de estado intrusivas ni enlaces de marca o pie de página.

    Personalización Flexible: Ajustes integrados para paleta de colores (fondo, texto/acento, widgets), tamaño y tipo de fuente en listas (Monospace, Sans-serif, Serif), anclaje de apps favoritas (máx. 3) y modo de búsqueda global de aplicaciones en el teléfono.

    Opción "No Dormir": Evita que la pantalla se apague automáticamente configurándolo desde las preferencias.

🛠️ Tecnologías

    Lenguaje: Kotlin

    Plataforma: Android (Activities, Intents, SharedPreferences, Vistas personalizadas con Canvas)

    Arquitectura y UI: AppCompat, Selector de Idioma Dinámico en Tiempo de Ejecución, Modo Inmersivo Estaky

🚀 Instalación y Uso

    Clona el repositorio:
    Bash

    git clone [https://github.com/apidato/CoreScreen.git](https://github.com/apidato/CoreScreen.git)

    Abre el proyecto en Android Studio.

    Compila, ejecuta y estabelécelo como tu aplicación de inicio (launcher) predeterminada desde los ajustes del sistema.