import isel.leic.utils.Time

object app{
    private var SCORE: Int = 0; private const val POINTSPERKILL = 2
    private var GAMERUN: Boolean = false
    private var AIMPOS = 1;
    private var AIMVALUE: Char? = null
    private const val SPAWNTIME = 2000
    private var TOPINVADERS = ""; private var BOTTOMIMVADERS = ""

    fun init(){
        TOPINVADERS = ""; BOTTOMIMVADERS = ""
        SCORE = 0; GAMERUN = false
        AIMPOS = 1; AIMVALUE = null;
        TUI.init()
        ScoreDisplay.init()
        CoinAcceptor.init()
        TUI.clearScreen()
        initScreen()
    }

    fun scoreUp(value: Int){
        SCORE += value
        ScoreDisplay.setScore(SCORE, false)
    }

    fun waitGameStart(): Boolean{
        val key = TUI.waitSpecificKey('#', 500)

        if(key){
            prepGameScreen()
            return true
        }

        return false

    }

    fun initScreen(){
        TUI.writeCorners("Space Invaders", top = true, left = true)
        TUI.writeCorners("Press #", top = false, left = true)
        while(true){
            if(waitGameStart()){
                prepGameScreen()
                break
            }
            ScoreDisplay.animation()
        }
    }

    fun prepGameScreen(){
        TUI.clearScreen()
        ScoreDisplay.setScore(0, false)
        TUI.writeCorners("C ", top = false, left = true)
        TUI.writeCorners("C ", top = true, left = true)
        resetAim()
        GAMERUN = true
    }

    fun spawnInvaders(){

        val row = (0..1).random()
        val invader = (0..9).random().toString()

        when (row) {
            0 -> {
                TOPINVADERS += invader
                TUI.writeCorners(TOPINVADERS, top = true, left = false)
            }
            1 -> {
                BOTTOMIMVADERS += invader
                TUI.writeCorners(BOTTOMIMVADERS, top = false, left = false)
            }

        }
        TUI.positionCursor(AIMPOS, 2)
    }

    fun checkGameOver(){
        if(TOPINVADERS.length >= 14 || BOTTOMIMVADERS.length >= 14){
            GAMERUN = false
        }
    }

    fun gameOverScreen(){
        TUI.clearScreen()
        TUI.writeCorners("Score: $SCORE", top = false, left = true)
        TUI.writeCorners("GAME OVER!", top = true, left = true)
    }

    fun handleInput() {

        when (val key = TUI.waitAnyKey(500)) {
            '*' -> {
                if (LCD.getCursorLine() == 1) {
                    TUI.positionCursor(2,2)
                    AIMPOS = 2
                    resetAim()
                }
                else if (LCD.getCursorLine() == 2) {
                    TUI.positionCursor(1, 2)
                    AIMPOS = 1
                    resetAim()
                }
            }

            '#' -> {processAim()}

            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', -> {
                TUI.writeChar(key)
                TUI.positionCursor(AIMPOS, 2)
                AIMVALUE = key
            }
            else -> return
        }

    }


    fun resetAim(){
        TUI.positionCursor(AIMPOS, 2)
        TUI.writeChar(' ')
        TUI.positionCursor(AIMPOS, 2)
        AIMVALUE = null
    }

    fun processAim() {
        when (AIMPOS) {
            1 -> {
                if (TOPINVADERS.isNotEmpty() && TOPINVADERS[0] == AIMVALUE) {
                    TOPINVADERS = TOPINVADERS.drop(1)
                    TUI.writeCorners(" $TOPINVADERS", top = true, left = false)
                    scoreUp(POINTSPERKILL)
                    resetAim()
                }
            }

            2 -> {
                if (BOTTOMIMVADERS.isNotEmpty() && BOTTOMIMVADERS[0] == AIMVALUE) {
                    BOTTOMIMVADERS = BOTTOMIMVADERS.drop(1)
                    TUI.writeCorners(" $BOTTOMIMVADERS", top = false, left = false)
                    scoreUp(POINTSPERKILL)
                    resetAim()
                }
            }
        }
    }

    fun startGame(){
        GAMERUN = true
        var lastSpawnTime = Time.getTimeInMillis()

        while(GAMERUN){
            handleInput()
            val currTime = Time.getTimeInMillis()
            if(currTime - lastSpawnTime > SPAWNTIME){
                spawnInvaders()
                lastSpawnTime = currTime
            }
            checkGameOver()
        }

        return
    }
}

fun main(){
    app.init()
    app.startGame()
    app.gameOverScreen()
}