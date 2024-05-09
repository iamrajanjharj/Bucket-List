import android.content.Context
import java.io.*
import java.util.*

const val FILENAME = "todolist.txt"

class ToDoList (var context: Context) {
    private var taskList: MutableList<String> = mutableListOf()

    fun addItem(item: String) {
        taskList.add(item)
    }

    fun getItems(): List<String> {
        return Collections.unmodifiableList(taskList)
    }

    fun clear() {
        taskList.clear()
    }

    fun saveToFile() {
        // Write list to file in internal storage
        val outputStream = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)
        writeListToStream(outputStream)
    }

    fun readFromFile() {
        try {
            // Read in list from file in internal storage
            val inputStream: FileInputStream = context.openFileInput(FILENAME)
            val reader = inputStream.bufferedReader()
            taskList.clear()
            reader.forEachLine { taskList.add(it) }
        } catch (ex: FileNotFoundException) {
            // Ignore
        }
    }

    private fun writeListToStream(outputStream: FileOutputStream) {
        val writer = PrintWriter(outputStream)
        for (item in taskList) {
            writer.println(item)
        }
        writer.close()
    }
}