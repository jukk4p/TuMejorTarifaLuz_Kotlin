package com.tumejortarifaluz.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable data object Splash : Screen
    @Serializable data object Onboarding1 : Screen
    @Serializable data object Onboarding2 : Screen
    @Serializable data object Onboarding3 : Screen
    @Serializable data object Home : Screen
    @Serializable data object ConsumptionSummary : Screen
    @Serializable data object UploadInvoice : Screen
    @Serializable data object ManualEntry : Screen
    @Serializable data object Validation : Screen
    @Serializable data object Comparison : Screen
    @Serializable data object Results : Screen
    @Serializable data class TariffDetail(val tariffId: String) : Screen
    @Serializable data object Settings : Screen
    @Serializable data object History : Screen
    @Serializable data object Favorites : Screen
    @Serializable data object Success : Screen
    @Serializable data class HelpArticle(val articleTitle: String) : Screen
    @Serializable data object HelpCenter : Screen
    @Serializable data object Chat : Screen
    @Serializable data object Rating : Screen
    @Serializable data object Login : Screen
    @Serializable data object Register : Screen
    @Serializable data object Profile : Screen
}
