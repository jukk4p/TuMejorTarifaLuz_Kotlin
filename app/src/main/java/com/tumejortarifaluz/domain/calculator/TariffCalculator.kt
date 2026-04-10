package com.tumejortarifaluz.domain.calculator

import com.tumejortarifaluz.ui.viewmodel.Tariff
import kotlin.math.roundToInt

data class CalculationResult(
    val totalBill: Double,
    val powerCost: Double,
    val energyCost: Double,
    val taxIEE: Double,
    val counterRental: Double,
    val socialBono: Double,
    val taxIVA: Double
)

object TariffCalculator {
    private const val IEE_RATE = 0.005 // Updated from 5.11%
    private const val IVA_RATE = 0.10 // Updated from 21%
    private const val BONO_SOCIAL_DAILY = 0.01912 // Standard daily rate
    private const val COUNTER_RENTAL_MONTHLY = 0.81

    fun calculateBill(
        tariff: Tariff,
        days: Int = 30,
        powerP1: Double,
        powerP2: Double,
        energyP1: Double,
        energyP2: Double,
        energyP3: Double
    ): CalculationResult {
        // 1. Término de Potencia
        val powerCostP1 = powerP1 * tariff.pricePowerP1 * days
        val powerCostP2 = powerP2 * tariff.pricePowerP2 * days
        val totalPowerCost = powerCostP1 + powerCostP2

        // 2. Término de Energía
        val energyCostP1 = energyP1 * tariff.priceEnergyP1
        val energyCostP2 = energyP2 * tariff.priceEnergyP2
        val energyCostP3 = energyP3 * tariff.priceEnergyP3
        val totalEnergyCost = energyCostP1 + energyCostP2 + energyCostP3

        // 3. Impuesto sobre la Electricidad (IEE)
        val subtotal = totalPowerCost + totalEnergyCost
        val taxIEE = subtotal * IEE_RATE

        // 4. Otros Costes Regulados
        val socialBono = BONO_SOCIAL_DAILY * days
        val counterRental = COUNTER_RENTAL_MONTHLY * (days / 30.0)

        // 6. Base Imponible
        val baseImponible = subtotal + taxIEE + socialBono + counterRental

        // 7. IVA
        val taxIVA = baseImponible * IVA_RATE

        // 8. Total Factura
        val total = baseImponible + taxIVA

        return CalculationResult(
            totalBill = roundToTwoDecimals(total),
            powerCost = roundToTwoDecimals(totalPowerCost),
            energyCost = roundToTwoDecimals(totalEnergyCost),
            taxIEE = roundToTwoDecimals(taxIEE),
            counterRental = roundToTwoDecimals(counterRental),
            socialBono = roundToTwoDecimals(socialBono),
            taxIVA = roundToTwoDecimals(taxIVA)
        )
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }
}
