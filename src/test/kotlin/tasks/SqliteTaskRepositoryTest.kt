package tasks

import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Epic("Список задач")
@Feature("SQLite: реальный репозиторий (in-memory)")
class SqliteTaskRepositoryTest {

    private lateinit var repository: SqliteTaskRepository

    @BeforeEach
    fun setUp() {
        repository = SqliteTaskRepository("jdbc:sqlite::memory:")
    }

    @AfterEach
    fun tearDown() {
        repository.close()
    }

    @Test
    @Story("Create/Read")
    @DisplayName("add присваивает id, getById возвращает сохранённую задачу")
    fun addAndReadBack() {
        val saved = repository.add(Task(title = "Написать тесты"))

        assertTrue(saved.id > 0)
        assertEquals(saved, repository.getById(saved.id))
    }

    @Test
    @Story("Read")
    @DisplayName("getAll возвращает все задачи в порядке добавления")
    fun getAllReturnsEverything() {
        repository.add(Task(title = "A"))
        repository.add(Task(title = "B"))

        val all = repository.getAll()

        assertEquals(listOf("A", "B"), all.map { it.title })
    }

    @Test
    @Story("Update")
    @DisplayName("update меняет поля задачи")
    fun updateChangesTask() {
        val saved = repository.add(Task(title = "A"))

        assertTrue(repository.update(saved.copy(title = "A+", done = true)))

        val reloaded = repository.getById(saved.id)!!
        assertEquals("A+", reloaded.title)
        assertTrue(reloaded.done)
    }

    @Test
    @Story("Delete")
    @DisplayName("delete удаляет задачу; повторное удаление возвращает false")
    fun deleteRemovesTask() {
        val saved = repository.add(Task(title = "A"))

        assertTrue(repository.delete(saved.id))
        assertNull(repository.getById(saved.id))
        assertFalse(repository.delete(saved.id))
    }
}
