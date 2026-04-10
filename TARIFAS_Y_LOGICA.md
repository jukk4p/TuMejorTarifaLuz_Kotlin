
# Tarifas y Lógica de Cálculo de la Aplicación

Este documento contiene la lista completa de las tarifas eléctricas utilizadas en la aplicación y una explicación detallada de cómo se calcula el coste final para el usuario.

## Listado de Tarifas (Sincronizado con Código)

|Compañía|Nombre Tarifa|Tipo|P. Punta (€/kW/día)|P. Valle (€/kW/día)|E. Punta (€/kWh)|E. Llano (€/kWh)|E. Valle (€/kWh)|Permanencia|URL de Contratación|
|:---|:---|:---|:---|:---|:---|:---|:---|:---|:---|
|Niba|Zen|Fijo (1 Periodo)|0.097|0.047|0.118|0.118|0.118|No|https://niba.es/luz-y-gas|
|Niba|Tres|3 Periodos|0.097|0.047|0.195|0.116|0.079|No|https://niba.es/luz-y-gas|
|Octopus|Relax|Fijo (1 Periodo)|0.098|0.098|0.098|0.098|0.098|No|https://octopusenergy.es/precios|
|Imagina|Tarifa base Sin Horas|Fijo (1 Periodo)|0.105|0.048|0.105|0.105|0.105|No|https://ofertas.imaginaenergia.com/plan-online-imagina/|
|Imagina|Tarifa base Noches y Findes|3 Periodos|0.101|0.022|0.177|0.103|0.069|No|https://ofertas.imaginaenergia.com/plan-online-imagina/|
|Visalia|Luz Fijo 24h|Fijo (1 Periodo)|0.060274|0.060274|0.097999|0.097999|0.097999|No|https://visalia.es/luz/fijo24horas/|
|Visalia|Luz 3 Periodos|3 Periodos|0.075903|0.001987|0.195822|0.114195|0.086543|No|https://visalia.es/luz/luz-3-periodos/|
|Repsol|Ahorro Plus|Fijo (1 Periodo)|0.081918|0.081918|0.1199|0.1199|0.1199|No|https://www.repsol.es/particulares/hogar/luz-y-gas/tarifas/tarifa-ahorro-plus/|
|Repsol|Solar con Batería Virtual|Fijo (1 Periodo)|0.81918|0.81918|0.1299|0.1299|0.1299|No|https://www.repsol.es/particulares/hogar/energia-solar/tarifas/tarifa-solar-bateria-virtual/|
|Energía Nufri|Con Horarios|3 Periodos|0.089601|0.034864|0.204892|0.128326|0.093757|No|https://www.energianufri.com/es/tarifas-luz|
|Energía Nufri|Sin Horarios|Fijo (1 Periodo)|0.089601|0.034864|0.138271|0.138271|0.138271|No|https://www.energianufri.com/es/tarifas-luz|
|Energía Nufri|Flex|3 Periodos|0.094533|0.046371|0.191853|0.115288|0.080719|No|https://www.energianufri.com/es/landing/mas-tarifas-luz-gas|
|Energía Nufri|Calma|Fijo (1 Periodo)|0.094533|0.046371|0.125233|0.125233|0.125233|No|https://www.energianufri.com/es/landing/mas-tarifas-luz-gas|
|Iberdrola|Plan Online Tres Periodos|3 Periodos|0.091074|0.013483|0.194|0.136|0.09999|No|https://www.iberdrola.es/luz/plan-online-tres-periodos|
|Iberdrola|Plan Online|Fijo (1 Periodo)|0.108192|0.057507|0.1099|0.1099|0.1099|No|https://www.iberdrola.es/luz/plan-online|
|Endesa|Conecta Luz|Fijo (1 Periodo)|0.09466|0.09466|0.119999|0.119999|0.119999|No|https://www.endesa.com/es/luz-y-gas/luz/conecta-de-endesa|
|Endesa|Fija 24h Online|Fijo (1 Periodo)|0.09466|0.09466|0.099999|0.099999|0.099999|No|https://www.endesa.com/es/luz-y-gas/luz/one/tarifa-one-luz|
|Endesa|Conecta 3 Periodos|3 Periodos|0.073644|0.073644|0.1656|0.0972|0.0738|No|https://www.endesa.com/es/luz-y-gas/luz/one/tarifa-one-luz-3periodos|
|Naturgy|Tarifa Noche|3 Periodos|0.12303|0.037337|0.1802|0.1072|0.0718|No|https://www.naturgy.es/hogar/luz/tarifa_noche|
|Energya VM|Formula Fija 3 Periodos Luz|3 Periodos|0.0915|0.003|0.16915|0.13515|0.10115|No|https://www.energyavm.es/luz/formula-fija-3-periodos-luz/|
|Energya VM|Formula 24h|Fijo (1 Periodo)|0.10367|0.05185|0.12519|0.12519|0.12519|No|https://www.energyavm.es/luz/formula-fija-24-horas-luz/|
|Total Energies|A tu Aire Luz Ahorro|3 Periodos|0.072575|0.072575|0.173572|0.10393|0.076176|No|https://www.totalenergies.es/es/hogares/tarifas-luz/a-tu-aire-programa-tu-ahorro|
|Total Energies|A tu Aire Luz Siempre|Fijo (1 Periodo)|0.072575|0.072575|0.1099|0.1099|0.1099|No|https://www.totalenergies.es/es/hogares/tarifas-luz/a-tu-aire-siempre|
|Esluz|Tarifa Solar 2.0|3 Periodos|0.080533|0.007407|0.187021|0.135066|0.085298|No|https://esluz.es/tarifa-solar-2-0/|
|Neolux Energy|Vehiculo Eléctrico|3 Periodos|0.0950|0.0850|0.199|0.179|0.039|No|https://neoluxenergy.com/comercializadora-energia-renovable/|
|Comercializadoras de Referencia|PVPC - Mercado Regulado|3 Periodos|0.0844|0.002|0.1959|0.1269|0.1089|No|https://sede.cnmc.gob.es/listado/censo/10|

---

## Lógica de Cálculo de Coste de la Factura

La aplicación calcula el coste total de una factura eléctrica simulada siguiendo estos pasos, que reflejan cómo se estructura una factura real en España.

Los datos de entrada son:
- `DÍAS_FACTURADOS`: Días del periodo (por defecto, 30).
- `POTENCIA_P1_kW`: Potencia contratada en periodo Punta.
- `POTENCIA_P2_kW`: Potencia contratada en periodo Valle.
- `ENERGÍA_P1_kWh`: Consumo de energía en periodo Punta.
- `ENERGÍA_P2_kWh`: Consumo de energía en periodo Llano.
- `ENERGÍA_P3_kWh`: Consumo de energía en periodo Valle.

El cálculo se desglosa en los siguientes apartados:

### 1. Término de Potencia
Es un coste fijo que depende de la potencia contratada. Se calcula multiplicando la potencia (en kW) por el precio del kW/día de la tarifa y por el número de días facturados.
- `Coste Potencia Punta = POTENCIA_P1_kW * precio_potencia_punta * DÍAS_FACTURADOS`
- `Coste Potencia Valle = POTENCIA_P2_kW * precio_potencia_valle * DÍAS_FACTURADOS`
- **Total Término de Potencia = Coste Potencia Punta + Coste Potencia Valle**

### 2. Término de Energía
Es un coste variable que depende del consumo eléctrico. Se calcula multiplicando los kWh consumidos en cada periodo por el precio de la energía (€/kWh) de ese periodo.
- `Coste Energía Punta = ENERGÍA_P1_kWh * precio_energia_punta`
- `Coste Energía Llano = ENERGÍA_P2_kWh * precio_energia_llano`
- `Coste Energía Valle = ENERGÍA_P3_kWh * precio_energia_valle`
- **Total Término de Energía = Coste Energía Punta + Coste Energía Llano + Coste Energía Valle**
- *Nota: Para las tarifas de precio fijo (1 solo periodo), se utiliza el mismo `precio_energia_punta` para los tres periodos.*

### 3. Impuesto sobre la Electricidad (IEE)
Es un impuesto regulado por el gobierno que se aplica sobre la suma de los términos de potencia y energía. Actualmente, el tipo impositivo es del **0.5%**.
- `Subtotal (Potencia + Energía) = Total Término de Potencia + Total Término de Energía`
- **Impuesto Eléctrico = Subtotal * 0.005**

### 4. Otros Costes Regulados
- **Financiación del Bono Social:** Un pequeño coste fijo diario para financiar el bono social. En la app se usa `0.01912 €/día`.
- **Alquiler del Contador:** Un coste fijo mensual por el alquiler del equipo de medida. En la app se usa `0.81 €/mes` para potencias hasta 15kW.

### 5. Cuota de la Compañía (Fee)
Algunas tarifas pueden incluir una cuota mensual fija (`fee`). Si la tarifa la tiene, se suma al total.

### 6. Base Imponible
Es la suma de todos los costes anteriores antes de aplicar el IVA.
- **Base Imponible = Subtotal (Potencia + Energía) + Impuesto Eléctrico + Alquiler Contador + Coste Bono Social + Fee Mensual**

### 7. IVA (Impuesto sobre el Valor Añadido)
Impuesto final que se aplica sobre la Base Imponible. Actualmente, para la mayoría de suministros domésticos es del **10%**.
- **IVA = Base Imponible * 0.10**

### 8. Total Factura
Es la suma de la Base Imponible más el IVA. Este es el valor final que se muestra en la comparativa.
- **Total Factura = Base Imponible + IVA**

El resultado final se redondea a dos decimales.
