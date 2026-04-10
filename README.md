# TuMejorTarifaLuz ⚡

TuMejorTarifaLuz es una aplicación Android desarrollada en **Kotlin** con **Jetpack Compose** diseñada para ayudar a los usuarios en España a comparar y encontrar la mejor tarifa eléctrica según su consumo real.

> [!NOTE]
> Esta aplicación es una **adaptación a Kotlin de la versión web** de [tumejortarifaluz.es](https://www.tumejortarifaluz.es), ofreciendo una experiencia simplificada y optimizada para dispositivos Android.

## 🚀 Características

- **Comparador Inteligente**: Analiza tu consumo en diferentes periodos (Punta, Llano, Valle) y lo compara con las tarifas más competitivas del mercado.
- **Base de Datos de Tarifas Actualizada**: Incluye tarifas de las principales comercializadoras (Niba, Octopus, Imagina, Visalia, Repsol, Iberdrola, Endesa, etc.).
- **Escaneo de Facturas**: (En desarrollo) Sube tu factura y deja que la app extraiga los datos automáticamente.
- **Cálculos Precisos**: Implementa la lógica real de facturación eléctrica en España, incluyendo Impuesto Eléctrico (IEE), Bono Social, Alquiler de Contador e IVA.
- **Gráficos de Consumo**: Visualiza tu gasto y potencia contratada de forma intuitiva.

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose (Modern Android UI)
- **Arquitectura**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Inyección de Dependencias**: Hilt (Dagger)
- **Base de Datos**: Room (SQLite)
- **Networking**: Corrutinas de Kotlin + Flow
- **Navegación**: Compose Navigation
- **Serialización**: Kotlin Serialization

## 📊 Lógica de Cálculo

La aplicación desglosa cada factura en:
1. **Término de Potencia**: Coste fijo por los kW contratados.
2. **Término de Energía**: Coste variable según los kWh consumidos.
3. **Impuesto sobre la Electricidad (IEE)**: 0.5% sobre la suma anterior.
4. **Costes Regulados**: Bono social y alquiler de contador.
5. **IVA**: Aplicado sobre la base imponible (10% por defecto).

Puedes encontrar más detalles sobre la lógica en [TARIFAS_Y_LOGICA.md](./TARIFAS_Y_LOGICA.md).

## 📦 Estructura del Proyecto

- `app/src/main/java/com/tumejortarifaluz/`
  - `di/`: Módulos de inyección de dependencias.
  - `domain/`: Lógica de negocio (calculadoras, modelos).
  - `ui/`: Componentes, pantallas y ViewModels.
  - `navigation/`: Definición de rutas de la app.
  - `util/`: Utilidades generales.

## 🛠️ Instalación y Uso

1. Clona el repositorio.
2. Abre el proyecto en **Android Studio (Ladybug o superior)**.
3. Sincroniza con Gradle.
4. Ejecuta en un emulador o dispositivo físico.

---

Desarrollado con ❤️ para ahorrar en la factura de la luz.
