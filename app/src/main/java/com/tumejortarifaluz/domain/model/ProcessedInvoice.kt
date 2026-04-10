package com.tumejortarifaluz.domain.model

data class ProcessedInvoice(
    val company: String? = null,
    val cups: String? = null,
    val powerP1: Double = 4.6,
    val powerP2: Double = 4.6,
    val energyP1: Double = 0.0,
    val energyP2: Double = 0.0,
    val energyP3: Double = 0.0,
    val days: Int = 30,
    val totalAmount: String? = null
)
