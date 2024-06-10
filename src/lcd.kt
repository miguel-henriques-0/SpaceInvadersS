object LCD { // Escreve no LCD usando a interface a 4 bits
    // Dados do display
    private const val LINES = 2
    private const val COLS = 16
    private const val LCDDATASIZE = 9

    // Posição do cursor
    private var CURSORLINE = 1
    private var CURSORCOLUMN = 1
    // Guarda a coluna onde o cursor estava antes de mudar de linha
    private var PREVCURSORCOLUMN = 1

    // Códigos dos comandos do LCD
    private const val CMDFUNCTIONSET = 59
    private const val CMDLCDOFF = 8
    private const val CMDLCDON = 15
    private const val CMDCLEARDISPLAY = 1
    private const val CMDESETENTRYMODE = 6
    private const val CMDSETCURSORHOME = 2

    // Masks UsbPort
    private const val RS_MASK = 0x40
    private const val E_MASK = 0x20
    private const val REG_LOW = 0x08
    private const val REG_CLK = 0x10

    // Define se a interface é Série ou Paralela
    private const val SERIAL_INTERFACE = true

    // Funções de consulta às variáveis privadas
    fun getCursorLine(): Int = CURSORLINE
    fun getCursorColumn(): Int = CURSORCOLUMN
    fun getPreviousCursorColumn(): Int = PREVCURSORCOLUMN
    fun getTotalLines(): Int = LINES
    fun getScreenColumns(): Int = COLS

    // Escreve um byte de comando/dados no LCD em paralelo
    private fun writeByteParallel(rs: Boolean, data: Int){
        val dataHigh = (data and 0xF0) ushr 4
        val dataLow = data and 0x0F

        if(rs){
            HAL.setBits(RS_MASK)
        }
        else{
            HAL.clrBits(RS_MASK)
        }

        HAL.setBits(E_MASK)
        HAL.writeBits(REG_LOW, dataHigh)
        HAL.setBits(REG_CLK)
        HAL.clrBits(REG_CLK)
        HAL.writeBits(REG_LOW, dataLow)
        HAL.setBits(REG_CLK)
        HAL.clrBits(REG_CLK)
        HAL.clrBits(E_MASK)
        HAL.setBits(E_MASK)

    }

    // Escreve um byte de comando/dados no LCD em série
    private fun writeByteSerial(rs: Boolean, data: Int) {
        var bitSeq = data
        val size = LCDDATASIZE

        if(rs){
            bitSeq = (bitSeq shl 1) or 0x001
        } else
            bitSeq = (bitSeq shl 1) and 0xFFE

        SerialEmitter.send(SerialEmitter.Destination.LCD, bitSeq, size)
    }

    // Escreve um byte de comando/dados no LCD
    private fun writeByte(rs: Boolean, data: Int){
        if(!SERIAL_INTERFACE)
            writeByteParallel(rs, data)
        else
            writeByteSerial(rs, data)
    }

    // Escreve um comando no LCD
    private fun writeCMD(data: Int){
        writeByte(false, data)
    }

    // Escreve um dado no LCD
    private fun writeDATA(data: Int){
        writeByte(true, data)
        CURSORCOLUMN++
    }

    // Envia a sequência de iniciação para comunicação a 8 bits
    fun init(){
        HAL.init()
        SerialEmitter.init()
        writeCMD(CMDFUNCTIONSET)        // Function set 1
        writeCMD(CMDFUNCTIONSET)        // Function set 2
        writeCMD(CMDFUNCTIONSET)        // Function set 3
        writeCMD(CMDFUNCTIONSET)        // Function set 4 (N = 2 (2 lines); F = 0 (font)
        writeCMD(CMDLCDOFF)             // Display off
        writeCMD(CMDCLEARDISPLAY)       // Display clear
        writeCMD(CMDESETENTRYMODE)      // Entry mode set
        writeCMD(CMDLCDON)              // Display on
    }

    // Escreve um caráter na posição corrente
    fun write(c: Char){
        writeDATA(c.code)
    }

    // Escreve uma string na posição corrente
    fun write(text: String){
        // Percorre cada char da string
        for(char in text){
            write(char)     // Escreve o char no display
        }
    }

    // Envia comando para posicionar cursor (‘line’:0..LINES-1 , ‘column’:0..COLS-1)
    fun cursor(line: Int, column: Int){
        val bit7Mask = 0x80     // A instrução para definir o endereço da DDRAM tem sempre o bit7 a 1
        var data = 0

        // Verifica se a linha e coluna são valores válidos
//        if(line > LINES || column > COLS) {
//            return
//        }

        // Caso vá ocorrer troca de linha guarda o número de caracteres escritos na linha atual
        if(line != CURSORLINE)
            PREVCURSORCOLUMN = CURSORCOLUMN

        // A linha 1 começa com o endereço 0x00 (0), a linha 2 começa com o endereço 0x40 (64)
        if(line == 2){
            data = bit7Mask or 0x40 + column - 1      // Subtrai-se 1 para que a linha 1 corresponda à posição y=0
            CURSORLINE = 2                      // Atualiza a linha do cursor para 2

        }
        else{
            data = bit7Mask or column-1
            CURSORLINE = 1                      // Atualiza linha do cursor para 1

        }

        CURSORCOLUMN = column                   // Atualiza a coluna do cursor
        writeCMD(data)

    }

    // Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    fun clear(){
        writeCMD(CMDCLEARDISPLAY)     // Limpa display
        writeCMD(CMDSETCURSORHOME)    // Retorna o cursor a (0,0)

        // Coloca as variáveis de posição do cursor em linha = 1, coluna = 1
        CURSORLINE = 1
        CURSORCOLUMN = 1

        // Sendo que o cursor é colocado em (1,1), dá reset a PREVCURSORCOLUMN
        PREVCURSORCOLUMN = 1
    }

}

fun main(){
    LCD.init()
    while(true){
        LCD.write("Space Invaders!")
        Thread.sleep(1000)
        LCD.clear()
        Thread.sleep(1000)
    }
}


