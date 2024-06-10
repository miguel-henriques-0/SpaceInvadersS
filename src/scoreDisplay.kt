
object ScoreDisplay { // Controla o mostrador de pontuação.

    // Inicia a classe, estabelecendo os valores iniciais.
    fun init(){
        SerialEmitter.init()
        off(true)
        off(false)
    }

    // Envia sequência de bits do comando mais dados para o Serial Emitter
    private fun sendByte(value: Int){
        val cmdBits = (value shr 4) and 0x07
        val dataBits = (value shl 3)
        val data = cmdBits or dataBits

        SerialEmitter.send(SerialEmitter.Destination.SCORE, data, 7)

    }

    // Envia o comando para atualizar o Score Display
    private fun updateScoreDisplay() = sendByte(96)

    // Animação para quando o Score Display não estã a ser usado
    fun animation(){
        setScore(111111, false)
        //Thread.sleep(300)
        setScore(0xEEEEE, true)
        //Thread.sleep(300)
        setScore(0xDDDDDD, true)
        //Thread.sleep(300)
        setScore(0xCCCCCC, true)
        //Thread.sleep(300)
        setScore(0xBBBBBB, true)
        //Thread.sleep(300)
        setScore(0xAAAAAA, true)
        //Thread.sleep(300)
    }

    // Envia comando para atualizar o valor do mostrador de pontuação
    fun setScore(value: Int, hex: Boolean){

    var n = value
    var digits = 0

    if(!hex) {
        while (n > 0 || digits < 6) {
            val send = (digits.shl(4) or n % 10)
            sendByte(send)
            digits++
            n /= 10
        }
    }
    else{
        var digit = 0
        val hexChar = (value and 0x0F).shr(digits)

        while(digit < 6){
            val send = digit.shl(4) or hexChar
            sendByte(send)
            digit++
        }
    }

        updateScoreDisplay()
    }

    // Envia comando para desativar/ativar a visualização do mostrador de pontuação
    fun off(value: Boolean){
        val status = if (value) 113 else 112
        sendByte(status)
    }

}

fun main(){
    ScoreDisplay.init()
    while(true){
        ScoreDisplay.animation()
    }
}