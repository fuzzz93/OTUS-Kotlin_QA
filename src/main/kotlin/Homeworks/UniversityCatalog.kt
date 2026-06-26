package Homeworks

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.regex.Pattern

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
    val country = readlnOrNull()

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // Новый драйвер: клиент создаётся через MongoClient.create(...)
    val mongoClient = MongoClient.create("mongodb://localhost:27017")

    try {
        val url = "http://universities.hipolabs.com/search?country=${country?.replace(" ", "%20")}"
        val universities: List<University> = client.get(url).body()

        if (universities.isEmpty()) {
            println("По стране '$country' ничего не найдено. Проверьте название (например: Russian Federation).")
            return@runBlocking
        }

        // --- Сохраняем в MongoDB ---
        val database = mongoClient.getDatabase("test")
        val collection = database.getCollection<University>("universities")
        collection.drop() // очищаем коллекцию перед вставкой
        collection.insertMany(universities)
        println("Сохранено в MongoDB: ${collection.countDocuments()} университетов")

        // --- Поиск по названию ---
        println("Введите часть названия университета для поиска:")
        val search = readlnOrNull()?.trim() ?: ""
        if (search.isNotEmpty()) {
            val pattern = Pattern.compile(".*${Pattern.quote(search)}.*", Pattern.CASE_INSENSITIVE)
            val results = collection.find(Filters.regex("name", pattern)).toList()
            println("Найдено по запросу '$search': ${results.size}")
            results.forEachIndexed { i, uni ->
                println("${i + 1}. ${uni.name} (${uni.country}, ${uni.stateProvince}) — сайт: ${uni.web_pages.firstOrNull() ?: "нет сайта"}")
            }
        } else {
            println("Пустой поисковый запрос, поиск не выполнен.")
        }
    } catch (e: Exception) {
        println("Ошибка при запросе или обработке данных: $e")
    } finally {
        client.close()
        mongoClient.close()
    }
}