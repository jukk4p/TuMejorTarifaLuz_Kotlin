package com.tumejortarifaluz.ui.utils

import com.tumejortarifaluz.R

object LogoMapper {
    fun getLogoForCompany(company: String, isDark: Boolean = true): Any {
        val companyClean = company.lowercase().trim()
        val isDarkSuffix = if (isDark) "dark" else "base"
        
        return when (companyClean) {
            "endesa" -> "file:///android_asset/logos/logo_endesa.png"
            "iberdrola" -> "file:///android_asset/logos/logo_iberdrola.png"
            "naturgy" -> "file:///android_asset/logos/logo_naturgy.png"
            "repsol" -> "file:///android_asset/logos/logo_repsol_$isDarkSuffix.png"
            "octopus" -> "file:///android_asset/logos/logo_octopus_$isDarkSuffix.png"
            "totalenergies", "total energies" -> "file:///android_asset/logos/logo_total_energies.png"
            "chc energia", "chcenergia", "chc energía" -> "file:///android_asset/logos/logo_chcenergia.png"
            "energia nufri", "energianufri", "energía nufri", "nufri" -> "file:///android_asset/logos/logo_energianufri_$isDarkSuffix.png"
            "energia vm", "energiavm", "energya vm" -> "file:///android_asset/logos/logo_energiavm.png"
            "esluz" -> "file:///android_asset/logos/logo_esluz.png"
            "imagina energia", "imaginaenergia", "imagina" -> "file:///android_asset/logos/logo_imaginaenergia.png"
            "niba" -> "file:///android_asset/logos/logo_niba_$isDarkSuffix.png"
            "visalia", "doméstica - visalia" -> "file:///android_asset/logos/logo_visalia.png"
            "neolux", "neolux energy", "neoluxenergy" -> {
                if (isDark) "file:///android_asset/logos/logo_neoluxenergy_dark.png"
                else "file:///android_asset/logos/logo_neoluxenergy_base.webp"
            }
            else -> {
                if (companyClean.contains("referencia") || companyClean.contains("pvpc")) {
                    "file:///android_asset/logos/logo_cor.svg"
                } else {
                    R.drawable.logo_app
                }
            }
        }
    }
}
