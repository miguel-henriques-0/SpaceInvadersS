import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.util.Formatter
import java.util.Scanner

object FileAccess{

    fun readFile(file: String): Array<String> {

        var linesArr = arrayOf<String>()

        // Open the FileReader and Scanner for the inputFile
        var `in`: Scanner? = null

        try {
            `in` = Scanner(FileReader(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            println("File missing: $file")
            System.exit(-1)
        }

        while (`in`!!.hasNextLine()) {
            linesArr += `in`.nextLine()
        }

        return linesArr
    }

    fun writeStringToFile(file: String, content: String) {
        FileWriter(file).use {
            it.write(content)
        }
    }

    fun writeArrayToFile(filename: String, array: ArrayList<String>){

        val file = File(filename)

        file.printWriter().use { out ->
            array.forEach { line -> out.println(line)}
        }

    }

}

fun main(args: Array<String>) {
    val a = arrayOf("Aaaa", "bbbb", "cccc")
    //FileAccess.writeArrayToFile("src/scores.txt", a)
}