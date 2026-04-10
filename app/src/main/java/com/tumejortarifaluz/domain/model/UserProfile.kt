package com.tumejortarifaluz.domain.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val cups: String = "",
    val currentCompany: String = "",
    val energyP1: Double = 0.0,
    val energyP2: Double = 0.0,
    val energyP3: Double = 0.0,
    val powerP1: Double = 0.0,
    val powerP2: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
