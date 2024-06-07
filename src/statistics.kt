
object Statistics{

    private const val STATSFILE = "src/statistics.txt"

    fun readCoins(): Int{
        val fileContent = FileAccess.readFile(STATSFILE)
        val stringCoins = fileContent[1]
        val coins = stringCoins.split(" ")[1]
        return coins.toInt()
    }

    fun incrementCoins(){
        val coins = readCoins()
    }

    fun readNumberOfGames(): Int{
        val fileContent = FileAccess.readFile(STATSFILE)
        val stringGames = fileContent[0]
        val numberOfGames = stringGames.split(" ")[1]
        return numberOfGames.toInt()
    }

}


fun main(){
    print(Statistics.readCoins())
    print(Statistics.readNumberOfGames())
}