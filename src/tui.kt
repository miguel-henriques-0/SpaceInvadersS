
object TUI{

    fun init(){
        LCD.init()
        KBD.init()
    }

    fun clearScreen(){
        LCD.clear()
    }

    fun screenOff(){
        LCD.displayOff()
    }

    fun cursorOutOfScreen(){
        positionCursor(1, 30)
    }

    fun clearLine(line: Int){
        when(line){
            1 -> {
                LCD.cursor(1,1)
            }
            2 -> {
                LCD.cursor(2,1)
            }
            else -> return
        }
        LCD.write("                ")
    }

    fun writeChar(char: Char){
        LCD.write(char)
    }

    fun waitAnyKey(time: Long): Char {
        return KBD.waitKey(time)
    }

    fun writeCorners(text: String, top: Boolean, left: Boolean){
        when(top){
            true -> when(left){
                true ->
                    LCD.cursor(1,1)
                false ->
                    LCD.cursor(1, LCD.getScreenColumns()-text.length+1)
            }
            false -> when(left){
                true ->
                    LCD.cursor(2,1)
                false ->
                    LCD.cursor(2,LCD.getScreenColumns()-text.length+1)

            }
        }

        LCD.write(text)
    }

    fun waitSpecificKey(key: Char, time: Long): Boolean{
        return KBD.waitKey(time) == key
    }

    fun positionCursor(line: Int, column: Int){
        LCD.cursor(line, column)
    }

    fun keyToLCD(){

        val key = KBD.waitKey(100)

        if(key.code != 0){
            if( key == '*'){
                if(LCD.getCursorLine() == 1)
                    LCD.cursor(2, LCD.getPreviousCursorColumn())
                else if(LCD.getCursorLine() == 2)
                    LCD.cursor(1, LCD.getPreviousCursorColumn())
            }
            else if( key == '#'){
                LCD.clear()
            }
            else
                LCD.write(key)

        }
    }

}

fun main(){
    TUI.init()

    while(true){
        TUI.keyToLCD()
    }

}