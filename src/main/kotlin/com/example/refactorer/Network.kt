package com.example.refactorer

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class APIRequest(
    @SerializedName("prompt") val kotlinCode: String,
    @SerializedName("max_tokens") val maxTokens: Int = 150,
    @SerializedName("temperature") val temperature: Double = 0.0
)

data class APIResponse(
    @SerializedName("choices") val choices: List<Choice>
)

data class Choice(
    @SerializedName("text") val text: String
)

interface OpenAiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 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"
    )
    @POST("engines/davinci/completions")
    suspend fun getRefactoringSuggestion(@Body data: APIRequest): APIResponse
}

fun createOpenAiService(): OpenAiService {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(OpenAiService::class.java)
}

private const val BASE_URL = "https://api.openai.com/v1/"