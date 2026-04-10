# TuMejorTarifaLuz ⚡

TuMejorTarifaLuz es una aplicación Android de última generación desarrollada en **Kotlin** con **Jetpack Compose**, diseñada para ayudar a los usuarios en España a optimizar su gasto energético y encontrar la mejor tarifa eléctrica basada en su consumo real.

> [!IMPORTANT]
> **Modernización UI/UX (Abril 2026)**: La aplicación ha sido rediseñada completamente con un sistema de diseño "Slate & Teal", ofreciendo una experiencia premium y adaptativa tanto en **Modo Claro** como en **Modo Oscuro**.

## 🚀 Características Principales

- **Escaneo Inteligente con IA (Gemini)**: Olvídate de meter datos a mano. Adjunta tu factura en PDF o JPG y nuestra integración con la IA de Google extraerá automáticamente CUPS, consumos (P1, P2, P3), potencias y más.
- **Comparador en Tiempo Real**: Analiza tu consumo actual y lo enfrenta a las tarifas más competitivas de comercializadoras como Octopus, Niba, Imagina, Visalia, Repsol, Iberdrola, y más.
- **Sistema de Temas Adaptativo**: Interfaz elegante que respeta la configuración del sistema, utilizando degradados dinámicos y tokens de Material3 para una legibilidad perfecta.
- **Cálculos de Ingeniería Eléctrica**: Implementación exacta de la normativa española (IEE, Bono Social, Alquiler de Contador, IVA dinámico).
- **Dashboard de Ahorro**: Visualización clara de cuánto podrías ahorrar al año con cada oferta.

## ⚙️ Stack Tecnológico

- **Language**: Kotlin 2.1.0
- **UI Framework**: Jetpack Compose (Modern Design System)
- **AI Engine**: Google Gemini 1.5/2.5 Flash SDK
- **Architecture**: MVVM + Clean Architecture + Repository Pattern
- **DI**: Hilt (Dagger)
- **Persistence**: Room Database (SQLite)
- **Reactive Stream**: Kotlin Coroutines & Flow
- **Navigation**: Type-safe Compose Navigation

## 📊 Lógica de Cálculo y Ahorro

La aplicación realiza una simulación completa de tu factura:
1. **Término de Potencia**: Desglose por periodos de potencia contratada.
2. **Término de Energía**: Aplicación de precios por periodos (Punta, Llano, Valle).
3. **Impuestos**: IEE (0.5%) e IVA (según configuración del mercado).
4. **Cargos Fijos**: Gestión del Bono Social y alquiler de equipos de medida.

Consulta [TARIFAS_Y_LOGICA.md](./TARIFAS_Y_LOGICA.md) para profundizar en lógicas específicas.

## 🛠️ Instalación y Setup

1. **Clonado**: `git clone https://github.com/jukk4p/TuMejorTarifaLuz_Kotlin.git`
2. **Requisitos**: Android Studio Ladybug (o superior).
3. **API Key**: Agrega tu `GEMINI_API_KEY` en el archivo `local.properties` para habilitar el escaneo inteligente.
4. **Compilación**: Sincroniza con Gradle y ejecuta en tu dispositivo.

---

Desarrollado con ❤️ por **Iván González** para empoderar al consumidor eléctrico.
