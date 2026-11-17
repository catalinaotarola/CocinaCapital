package cl.DSY1105.cocinacapital.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClimaRespuesta(
    @SerializedName("current") val actual: ClimaActualDto?
)

data class ClimaActualDto(
    @SerializedName("temperature_2m") val temperatura: Double?,
    @SerializedName("weather_code") val codigo: Int?
)
