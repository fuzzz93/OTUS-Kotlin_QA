package tasks.api

import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.HttpException

@Epic("Список задач")
@Feature("REST API: MockWebServer + Retrofit")
class TaskApiMockWebServerTest {

    private lateinit var server: MockWebServer
    private lateinit var api: TaskApi

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = TaskApiFactory.create(server.url("/").toString())
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    @Story("Read")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /tasks возвращает список задач")
    fun getTasksReturnsList() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""[{"id":1,"title":"A","done":false},{"id":2,"title":"B","done":true}]"""),
        )

        val tasks = api.getTasks()

        assertEquals(2, tasks.size)
        assertEquals(Task(1, "A", done = false), tasks[0])
        assertTrue(tasks[1].done)

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/tasks", request.path)
    }

    @Test
    @Story("Read")
    @DisplayName("GET /tasks/{id} возвращает одну задачу")
    fun getTaskByIdReturnsTask() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"id":5,"title":"C","done":true}"""),
        )

        val task = api.getTask(5)

        assertEquals(Task(5, "C", done = true), task)
        assertEquals("/tasks/5", server.takeRequest().path)
    }

    @Test
    @Story("Create")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /tasks создаёт задачу и отправляет корректное тело")
    fun createTaskSendsBodyAndParsesResponse() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(201).setBody("""{"id":10,"title":"New","done":false}"""),
        )

        val created = api.createTask(NewTask("New"))

        assertEquals(Task(10, "New", done = false), created)

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/tasks", request.path)
        val body = request.body.readUtf8()
        assertTrue(body.contains("\"title\":\"New\""), "тело запроса: $body")
        assertTrue(body.contains("\"done\":false"), "тело запроса: $body")
    }

    @Test
    @Story("Delete")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /tasks/{id} возвращает 204 No Content")
    fun deleteTaskReturnsNoContent() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(204))

        val response = api.deleteTask(10)

        assertTrue(response.isSuccessful)
        assertEquals(204, response.code())

        val request = server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/tasks/10", request.path)
    }

    @Test
    @Story("Errors")
    @DisplayName("GET /tasks/{id} на 404 бросает HttpException")
    fun getTaskThrowsOnNotFound() {
        server.enqueue(MockResponse().setResponseCode(404))

        val exception = assertThrows(HttpException::class.java) {
            runBlocking { api.getTask(999) }
        }
        assertEquals(404, exception.code())
    }
}
