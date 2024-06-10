
object CoinAcceptor{

    private const val CMASK = 0x40
    private var COINS = 0

    fun getCoins(): Int = COINS

    fun init(){
        resetCoins()
    }

    fun resetCoins(){
        COINS = 0
    }

    fun checkCoin(): Boolean{
        if(HAL.isBit(CMASK)){
            COINS += 2
            HAL.setBits(CMASK)
            HAL.clrBits(CMASK)
            return true
        }
        return false
    }

}

fun main(){
    CoinAcceptor.init()
    while(true) {
        if(CoinAcceptor.checkCoin())
            println(CoinAcceptor.getCoins())
    }
}