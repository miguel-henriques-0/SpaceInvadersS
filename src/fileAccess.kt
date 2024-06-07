import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
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

}

fun main(args: Array<String>) {
    FileAccess.readFile("src/scores.txt")
}