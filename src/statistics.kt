import java.util.ArrayList

object Statistics{

    private const val STATSFILE = "src/statistics.txt"
    const val GAMESSTRING = "Jogos;"
    const val COINSSTRING = "Moedas;"

    fun readCoins(): Int{
        val fileContent = FileAccess.readFile(STATSFILE)
        val stringCoins = fileContent[1]
        val coins = stringCoins.split(';')[1]
        return coins.toInt()
    }

    fun incrementCoins(){
        val coins = readCoins()
    }

    fun readNumberOfGames(): Int{
        val fileContent = FileAccess.readFile(STATSFILE)
        val stringGames = fileContent[0]
        val numberOfGames = stringGames.split(';')[1]
        return numberOfGames.toInt()
    }

    fun saveStatsFile(coins: Int, games: Int){
        val a = ArrayList<String>()
        a.add("$GAMESSTRING$games")
        a.add("$COINSSTRING$coins")
        FileAccess.writeArrayToFile(STATSFILE, a)
    }

}


fun main(){
    print(Statistics.readCoins())
    print(Statistics.readNumberOfGames())
}