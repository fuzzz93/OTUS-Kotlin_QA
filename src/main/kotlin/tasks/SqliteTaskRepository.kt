package tasks

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class SqliteTaskRepository(
    url: String = "jdbc:sqlite:tasks.db",
) : TaskRepository, AutoCloseable {

    private val connection: Connection = DriverManager.getConnection(url)

    init {
        connection.createStatement().use { st ->
            st.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS tasks (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT    NOT NULL,
                    done  INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent(),
            )
        }
    }

    override fun add(task: Task): Task {
        connection.prepareStatement(
            "INSERT INTO tasks(title, done) VALUES(?, ?)",
            Statement.RETURN_GENERATED_KEYS,
        ).use { ps ->
            ps.setString(1, task.title)
            ps.setInt(2, task.done.toInt())
            ps.executeUpdate()
            ps.generatedKeys.use { rs ->
                val id = if (rs.next()) rs.getLong(1) else 0L
                return task.copy(id = id)
            }
        }
    }

    override fun getAll(): List<Task> {
        val result = mutableListOf<Task>()
        connection.createStatement().use { st ->
            st.executeQuery("SELECT id, title, done FROM tasks ORDER BY id").use { rs ->
                while (rs.next()) result += rs.toTask()
            }
        }
        return result
    }

    override fun getById(id: Long): Task? {
        connection.prepareStatement("SELECT id, title, done FROM tasks WHERE id = ?").use { ps ->
            ps.setLong(1, id)
            ps.executeQuery().use { rs ->
                return if (rs.next()) rs.toTask() else null
            }
        }
    }

    override fun update(task: Task): Boolean {
        connection.prepareStatement("UPDATE tasks SET title = ?, done = ? WHERE id = ?").use { ps ->
            ps.setString(1, task.title)
            ps.setInt(2, task.done.toInt())
            ps.setLong(3, task.id)
            return ps.executeUpdate() > 0
        }
    }

    override fun delete(id: Long): Boolean {
        connection.prepareStatement("DELETE FROM tasks WHERE id = ?").use { ps ->
            ps.setLong(1, id)
            return ps.executeUpdate() > 0
        }
    }

    override fun close() = connection.close()

    private fun Boolean.toInt() = if (this) 1 else 0

    private fun java.sql.ResultSet.toTask() =
        Task(getLong("id"), getString("title"), getInt("done") == 1)
}
