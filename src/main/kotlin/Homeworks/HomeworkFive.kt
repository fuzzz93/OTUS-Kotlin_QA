package Homeworks

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import org.litote.kmongo.*
import org.litote.kmongo.getCollection

@Serializable
data class University(
    val name: String,
    val country: String,
    val web_pages: List<String>,
    val domains: List<String>,
    @SerialName("state-province")
    val stateProvince: String? = null
)

fun main() = runBlocking {
    println("Введите страну для поиска университетов:")
    val country = readLine()

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    val url = "http://universities.hipolabs.com/search?country=${country?.replace(" ", "%20")}"
    try {
        val universities: List<University> = client.get(url).body()
        // --- Сохраняем в MongoDB ---
        val mongoClient = KMongo.createClient()
        val database = mongoClient.getDatabase("test")
        val collection = database.getCollection<University>("universities")
        collection.drop() // очищаем коллекцию перед вставкой
        collection.insertMany(universities)
        println("Сохранено в MongoDB: ${collection.countDocuments()} университетов")
        // --- Поиск по названию ---
        println("Введите часть названия университета для поиска:")
        val search = readLine()?.trim() ?: ""
        if (search.isNotEmpty()) {
            val results =
                collection.find(University::name regex ".*${search}.*".toRegex(RegexOption.IGNORE_CASE)).toList()
            println("Найдено по запросу '${search}': ${results.size}")
            results.forEachIndexed { i, uni ->
                println("${i + 1}. ${uni.name} (${uni.country}, ${uni.stateProvince}) — сайт: ${uni.web_pages.firstOrNull() ?: "нет сайта"}")
            }
        } else {
            println("Пустой поисковый запрос, поиск не выполнен.")
        }
        mongoClient.close()
    } catch (e: Exception) {
        println("Ошибка при запросе или обработке данных: ${e}")
    } finally {
        client.close()
    }
}

