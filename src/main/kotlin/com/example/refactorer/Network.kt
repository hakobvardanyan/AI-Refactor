package com.example.refactorer

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class APIRequest(
    @SerializedName("messages") val messages: List<RequestMessage>,
    @SerializedName("model") val model: String = "gpt-3.5-turbo",
    @SerializedName("temperature") val temperature: Double = 0.0
)

data class RequestMessage(
    @SerializedName("role") val role: String = "user",
    @SerializedName("content") val content: String,
)

data class APIResponse(
    @SerializedName("choices") val choices: List<Choice>
)

data class Choice(
    @SerializedName("message") val message: ResponseMessage
)

data class ResponseMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

interface OpenAiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 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"
    )
    @POST("chat/completions")
    suspend fun getRefactoringSuggestion(@Body data: APIRequest): APIResponse
}

private fun createOpenAiService(): OpenAiService {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(OpenAiService::class.java)
}

suspend fun getRefactoringSuggestion(selectedText: String): List<Choice> = openAiService.getRefactoringSuggestion(
    APIRequest(
        messages = listOf(
            RequestMessage(
                content = "Pretend you are senior engineer, please suggest a good alternative for the following code without any fairy tales: \n$selectedText"
            )
        )
    )
).choices

private val openAiService by lazy { createOpenAiService() }

private const val BASE_URL = "https://api.openai.com/v1/"