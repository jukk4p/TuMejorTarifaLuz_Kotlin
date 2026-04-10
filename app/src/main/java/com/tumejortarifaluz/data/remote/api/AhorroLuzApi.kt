package com.tumejortarifaluz.data.remote.api

import com.tumejortarifaluz.data.remote.model.TariffResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AhorroLuzApi {
    @GET("tariffs")
    suspend fun getTariffs(
        @Query("consumption_kwh") consumption: Int? = null,
        @Query("power_kw") power: Double? = null
    ): TariffResponse
}
