import isel.leic.UsbPort

object HAL{ // Virtualiza o acesso ao sistema UsbPort

    // Guarda o valor no output do UsbPort
    private var usbOutValue = 0

    // Inicializa a classe
    fun init(){
        if(usbOutValue == 0 && UsbPort.read() == 0x00){
            return
        }
        usbOutValue = 0
        UsbPort.write(0x00)
    }

    // Retorna true se o bit tiver o valor lógico ‘1’
    fun isBit(mask: Int): Boolean {
        return(readBits(mask) != 0)
    }

    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int): Int{
        return( UsbPort.read() and mask )
    }

    // Coloca os bits representados por mask no valor lógico ‘1’
    fun setBits(mask: Int){
        usbOutValue = usbOutValue or mask
        UsbPort.write(usbOutValue)
    }

    // Coloca os bits representados por mask no valor lógico ‘0’
    fun clrBits(mask: Int){
        usbOutValue = mask.inv() and usbOutValue
        UsbPort.write(usbOutValue)
    }

    // Escreve nos bits representados por mask os valores dos bits correspondentes em value
    fun writeBits(mask: Int, value: Int){
        usbOutValue = (usbOutValue and mask.inv()) or (value and mask)
        UsbPort.write(usbOutValue)
    }

}

fun main(args: Array<String>) {
    HAL.init()
    var value = 1
    while(true){
        Thread.sleep(100)
        HAL.writeBits(0xFF, value)
        value *= 2
        if(value > 0x80) value = 1
    }
}
