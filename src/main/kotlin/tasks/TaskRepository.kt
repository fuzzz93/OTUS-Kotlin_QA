package tasks

interface TaskRepository {
    fun add(task: Task): Task
    fun getAll(): List<Task>
    fun getById(id: Long): Task?
    fun update(task: Task): Boolean
    fun delete(id: Long): Boolean
}
