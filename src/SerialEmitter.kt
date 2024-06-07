object SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.
    enum class Destination {LCD, SCORE}

    private const val LCDSELMASK = 0x01
    private const val SCORESELMASK = 0x02
    private const val SCLKMASK = 0x10
    private const val SDXMASK = 0x08

    // Inicia a classe
    fun init(){
        HAL.init()
        HAL.setBits(LCDSELMASK)
        HAL.setBits(SCORESELMASK)
    }

    private fun sendBitSequence(data: Int, size: Int){

        var parity = 0

        for(i in 0..< size){
            // determina o bit a enviar
            val bitToSend = (data shr i) and 0x01
            parity += bitToSend

            // enviar 1 bit (SDK)
            if(bitToSend == 1)
                HAL.setBits(SDXMASK)
            else
                HAL.clrBits(SDXMASK)

            // clock (SCLK)
            HAL.setBits(SCLKMASK)
            HAL.clrBits(SCLKMASK)
        }

        var parityBit = 0

        if(parity % 2 != 0)
            parityBit = 1

        if(parityBit == 1)
            HAL.setBits(SDXMASK)
        else
            HAL.clrBits(SDXMASK)

        HAL.setBits(SCLKMASK)
        HAL.clrBits(SCLKMASK)

    }

    // Envia uma trama para o SerialReceiver identificado o destino em addr,os bits de dados em ‘data’ e em size o número de bits a enviar.
    fun send(addr: Destination, data: Int, size: Int){

        if(addr == Destination.LCD){
            HAL.clrBits(LCDSELMASK)
            sendBitSequence(data, size)
            HAL.setBits(LCDSELMASK)
        }

        else if(addr == Destination.SCORE){
            HAL.clrBits(SCORESELMASK)
            sendBitSequence(data, size)
            HAL.setBits(SCORESELMASK)
        }

    }

}

fun main(){
   SerialEmitter.send(SerialEmitter.Destination.SCORE,0x90,7)
}
