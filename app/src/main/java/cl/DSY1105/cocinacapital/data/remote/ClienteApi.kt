package cl.DSY1105.cocinacapital.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ClienteApi {
    private const val URL_CATALOGO = "http://10.0.2.2:8081/api/"
    private const val URL_RESENAS = "http://10.0.2.2:8082/api/"
    private const val URL_CLIMA = "https://api.open-meteo.com/"

    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun crear(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val catalogo: CatalogoApi by lazy { crear(URL_CATALOGO).create(CatalogoApi::class.java) }
    val resenas: ResenasApi by lazy { crear(URL_RESENAS).create(ResenasApi::class.java) }
    val clima: ClimaApi by lazy { crear(URL_CLIMA).create(ClimaApi::class.java) }
}
