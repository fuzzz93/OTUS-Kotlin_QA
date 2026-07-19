package tasks

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Epic("Список задач")
@Feature("SQLite: mock репозитория (MockK)")
class TaskServiceMockTest {

    private val repository = mockk<TaskRepository>()
    private val service = TaskService(repository)

    @Test
    @Story("Create")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("createTask сохраняет задачу и возвращает её с id")
    fun createTaskDelegatesToRepository() {
        every { repository.add(Task(title = "Купить хлеб")) } returns Task(1, "Купить хлеб")

        val created = service.createTask("Купить хлеб")

        assertEquals(Task(1, "Купить хлеб", done = false), created)
        verify(exactly = 1) { repository.add(Task(title = "Купить хлеб")) }
    }

    @Test
    @Story("Create")
    @DisplayName("createTask с пустым заголовком бросает исключение и не трогает репозиторий")
    fun createTaskRejectsBlankTitle() {
        assertThrows(IllegalArgumentException::class.java) {
            service.createTask("   ")
        }
        verify(exactly = 0) { repository.add(any()) }
    }

    @Test
    @Story("Read")
    @DisplayName("listTasks возвращает весь список из репозитория")
    fun listTasksReturnsRepositoryContent() {
        val tasks = listOf(Task(1, "A"), Task(2, "B", done = true))
        every { repository.getAll() } returns tasks

        assertEquals(tasks, service.listTasks())
        verify { repository.getAll() }
    }

    @Test
    @Story("Read")
    @DisplayName("getTask бросает NoSuchElementException, если задачи нет")
    fun getTaskThrowsWhenMissing() {
        every { repository.getById(42) } returns null

        assertThrows(NoSuchElementException::class.java) { service.getTask(42) }
    }

    @Test
    @Story("Update")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("completeTask помечает задачу выполненной и сохраняет её")
    fun completeTaskMarksDone() {
        every { repository.getById(1) } returns Task(1, "A", done = false)
        every { repository.update(any()) } returns true

        val result = service.completeTask(1)

        assertTrue(result.done)
        verify { repository.update(Task(1, "A", done = true)) }
    }

    @Test
    @Story("Delete")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("deleteTask удаляет существующую задачу")
    fun deleteTaskRemovesExisting() {
        every { repository.getById(1) } returns Task(1, "A")
        every { repository.delete(1) } returns true

        assertTrue(service.deleteTask(1))
        verify { repository.delete(1) }
    }

    @Test
    @Story("Delete")
    @DisplayName("deleteTask бросает исключение и не удаляет, если задачи нет")
    fun deleteTaskThrowsWhenMissing() {
        every { repository.getById(99) } returns null

        assertThrows(NoSuchElementException::class.java) { service.deleteTask(99) }
        verify(exactly = 0) { repository.delete(any()) }
    }
}
