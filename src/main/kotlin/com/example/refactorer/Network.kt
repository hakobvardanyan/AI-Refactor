package com.example.refactorer

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class APIRequest(
    @SerializedName("messages") val messages: List<RequestMessage>,
    @SerializedName("model") val model: String = "gpt-3.5-turbo-16k",
    @SerializedName("max_tokens") val maxTokens: Int = 2048,
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
        "Authorization: Bearer YOUR_API_KEY"
    )
    @POST("chat/completions")
    suspend fun request(@Body data: APIRequest): APIResponse
}

private fun createOpenAiService(): OpenAiService {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(OpenAiService::class.java)
}

suspend fun requestCodeImprovement(selectedText: String): List<Choice> = openAiService.request(
    APIRequest(
        messages = listOf(
            RequestMessage(
                content = "Pretend you are a senior engineer and suggest an improved alternative for the following code, but provide only full version code as plain text without code block syntax around it and any placeholders or ellipses: \n$selectedText"
            )
        )
    )
).choices

suspend fun requestCodeSuggestionAndExplanation(selectedText: String): List<Choice> = openAiService.request(
    APIRequest(
        messages = listOf(
            RequestMessage(
                content = "Pretend you are a senior engineer and suggest an improved alternative for the following code providing only the code block and brief explanation of it: \n$selectedText"
            )
        )
    )
).choices

private val openAiService by lazy { createOpenAiService() }

private const val BASE_URL = "https://api.openai.com/v1/"