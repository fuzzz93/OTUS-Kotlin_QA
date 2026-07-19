package tasks

data class Task(
    val id: Long = 0,
    val title: String,
    val done: Boolean = false,
)
