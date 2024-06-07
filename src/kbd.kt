object KBD { // Ler teclas. Métodos retornam ‘0’..’9’,’#’,’*’ ou NONE.

    // Valores das máscaras
    private const val KEYMASK = 0x0F
    private const val VALMASK = 0x10
    private const val ACKMASK = 0x80
    private const val NONE = 0.toChar()

    private val arrayKeys = arrayOf(
        '1', '4', '7',
        '*', '2', '5',
        '8', '0', '3',
        '6', '9', '#'
    )

    // Inicia a classe
    fun init(){
        HAL.init()
        HAL.clrBits(ACKMASK)
    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {

        var key = NONE

        if(HAL.isBit(VALMASK)) {
            val keyValue = HAL.readBits(KEYMASK)
            HAL.setBits(ACKMASK)
            if(keyValue in (0..11)){
                key = arrayKeys[keyValue]
            }

            if(!HAL.isBit(VALMASK)){
                HAL.clrBits(ACKMASK)
                return key
            }
        }

        return NONE
    }

    // Retorna a tecla premida, caso ocorra antes do ‘timeout’ (representado em milissegundos), ou NONE caso contrário.
    fun waitKey(timeout: Long): Char{
        val timeInit = System.currentTimeMillis()
        while(true) {
            val keyValue = getKey()
            if (keyValue != NONE) {
                return keyValue
            }
            else if((System.currentTimeMillis() - timeInit > timeout))
                return NONE
        }
    }

}

fun main(args: Array<String>) {
    KBD.init()
    while(true){
        print(KBD.waitKey(1000))
    }
}
