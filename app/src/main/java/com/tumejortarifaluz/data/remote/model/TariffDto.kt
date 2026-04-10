package com.tumejortarifaluz.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TariffDto(
    val id: String,
    val companyName: String,
    val planName: String,
    val type: String,
    val pricePowerP1: Double,
    val pricePowerP2: Double,
    val priceEnergyP1: Double,
    val priceEnergyP2: Double,
    val priceEnergyP3: Double,
    val contractUrl: String = "",
    val logoUrl: String? = null,
    val logoLightUrl: String? = null
)

@Serializable
data class TariffResponse(
    val tariffs: List<TariffDto>
)
