package tasks

class TaskService(private val repository: TaskRepository) {

    fun createTask(title: String): Task {
        require(title.isNotBlank()) { "Заголовок задачи не может быть пустым" }
        return repository.add(Task(title = title.trim()))
    }

    fun listTasks(): List<Task> = repository.getAll()

    fun getTask(id: Long): Task =
        repository.getById(id) ?: throw NoSuchElementException("Задача $id не найдена")

    fun completeTask(id: Long): Task {
        val updated = getTask(id).copy(done = true)
        repository.update(updated)
        return updated
    }

    fun deleteTask(id: Long): Boolean {
        getTask(id)
        return repository.delete(id)
    }
}
