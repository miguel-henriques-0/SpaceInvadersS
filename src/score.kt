import java.io.File

object Scores{

    private const val SCOREFILE = "src/scores.txt"
    private const val NUMBERSCORES = 20

    data class Score(var name: String, var points: Int)
    private var scores = ArrayList<Score>()

    fun init(){
        populateScoresFromFile(SCOREFILE)
    }

    fun getScores(): ArrayList<Score>{
        return scores
    }

    private fun compareScores(score1: Score, score2: Score): Int = score1.points - score2.points

    fun addScore(newScore: Score): Boolean{

        if(scores.isEmpty()){
            scores.add(newScore)
            return true
        }

        if(compareScores(scores[scores.size-1], newScore) > 0 && scores.size < NUMBERSCORES){
            scores.add(newScore)
            return true
        }

        for (i in 0..< scores.size) {
            if (compareScores(scores[i], newScore) < 0){
                scores.add(i, newScore)

                if(scores.size > NUMBERSCORES){
                    scores.removeAt(scores.size - 1)
                }

                return true
            }
        }

        return false

    }

    fun populateScoresFromFile(fileName: String){
        val lines = FileAccess.readFile(fileName)
        for(l in lines){
            val fields = l.split(";")
            val newScore = Score(fields[0], fields[1].toInt())
            addScore(newScore)
        }
    }

    fun writeScoresToFile(scores: ArrayList<Score>){
        val arrayString = ArrayList<String>()

        for(sc in 0..< scores.size){
            arrayString.add("${scores[sc].name};${scores[sc].points}")
        }

        FileAccess.writeArrayToFile(SCOREFILE, arrayString)

    }

}

fun main(){
    Scores.populateScoresFromFile("src/scores.txt")
    Scores.getScores().forEach { score -> println(score)}
    println(Scores.getScores().size)
}
