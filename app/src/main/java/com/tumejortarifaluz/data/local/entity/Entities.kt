package com.tumejortarifaluz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tariffs")
data class TariffEntity(
    @PrimaryKey val id: String,
    val company: String,
    val name: String,
    val type: String, // "Fijo (1 Periodo)" or "3 Periodos"
    val pricePowerP1: Double,
    val pricePowerP2: Double,
    val priceEnergyP1: Double,
    val priceEnergyP2: Double,
    val priceEnergyP3: Double,
    val contractUrl: String = "",
    val logoUrl: String? = null,
    val logoLightUrl: String? = null,
    val isFavorite: Boolean = false,
    val permanence: Boolean = false,
    val surplusPrice: Double = 0.0,
    val updatedAt: String = "",
    val pricePowerP1WithTaxes: Double = 0.0,
    val pricePowerP2WithTaxes: Double = 0.0,
    val priceEnergyP1WithTaxes: Double = 0.0,
    val priceEnergyP2WithTaxes: Double = 0.0,
    val priceEnergyP3WithTaxes: Double = 0.0
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val type: String,
    val status: String,
    val description: String,
    val company: String? = null,
    val cups: String? = null,
    val powerP1: Double = 3.5,
    val powerP2: Double = 3.5,
    val energyP1: Double = 0.0,
    val energyP2: Double = 0.0,
    val energyP3: Double = 0.0,
    val days: Int = 30,
    val totalAmount: String? = null,
    val estimatedSaving: String? = null
)
