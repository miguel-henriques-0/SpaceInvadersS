
object M{

    private const val MBIT = 0x80

    fun checkMaitenance(): Boolean{
        if(HAL.isBit(MBIT))
            return true
        else
            return false
    }

}

fun main(args: Array<String>) {
    LCD.init()
    while(true){
        if(M.checkMaitenance())
        TUI.writeCorners("aaaaaa", true, true)
    }
}