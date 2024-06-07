import isel.leic.utils.Time

object app{
    // Variáveis de jogo
    private var GAMERUN: Boolean = false
    private var SCORE: Int = 0; private const val POINTSPERKILL = 2
    private var COINS = 0
    private var NJOGOS = 0

    // Variáveis de mira
    private var AIMPOS = 1; private var AIMVALUE: Char? = null

    // Variáveis de inimigos
    private const val SPAWNTIME = 100
    private var TOPINVADERS = ""; private var BOTTOMIMVADERS = ""

    // Constantes de tempo
    private var STARTKEYWAITTIME: Long = 100
    private var GAMEKEYWAITTIME: Long = 100
    private var ANIMATIONTIME = 1500


    private const val NAMESCREENSTRING = "Nome: "
    private var LETTER = 'A'
    private var USERNAME = ""


    fun init(){
        TOPINVADERS = ""; BOTTOMIMVADERS = ""
        SCORE = 0; GAMERUN = false
        AIMPOS = 1; AIMVALUE = null;
        COINS = Statistics.readCoins()
        NJOGOS = Statistics.readNumberOfGames()

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
        val key = TUI.waitSpecificKey('#', STARTKEYWAITTIME)

        if(key){
            prepGameScreen()
            return true
        }

        return false

    }

    fun initScreen(){
        val initTime = System.currentTimeMillis()
        TUI.writeCorners("Space Invaders", top = true, left = true)
        TUI.writeCorners("Credits: $COINS$", top = false, left = false)
        while(true){
            if(waitGameStart()){
                prepGameScreen()
                break
            }
            if(CoinAcceptor.checkCoin()){
                COINS += 2
                TUI.writeCorners("$COINS$", top = false, left = false)
            }
            if(System.currentTimeMillis() - initTime > ANIMATIONTIME)
                ScoreDisplay.animation()
            if(M.checkMaitenance()){
                TUI.clearScreen()
            }
        }
    }

    fun prepGameScreen(){
        TUI.clearScreen()
        ScoreDisplay.setScore(0, false)
        TUI.writeCorners("[ ", top = false, left = true)
        TUI.writeCorners("[ ", top = true, left = true)
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

    fun handleNameInputs(){
        when (val key = TUI.waitAnyKey(GAMEKEYWAITTIME)) {
            '6' -> {
                LETTER++
                if(LETTER > 'Z'){
                    LETTER = 'A'
                }
                TUI.writeChar(LETTER)
                TUI.positionCursor(1, NAMESCREENSTRING.length + USERNAME.length + 1)
            }
            '4' -> {
                LETTER--
                if(LETTER < 'A'){
                    LETTER = 'Z'
                }
                TUI.writeChar(LETTER)
                TUI.positionCursor(1, NAMESCREENSTRING.length + USERNAME.length + 1)
            }
            '#' -> {
                USERNAME += LETTER
                TUI.positionCursor(1, NAMESCREENSTRING.length + USERNAME.length + 2)
                LETTER = 'A'
                TUI.writeChar(LETTER)
            }

            else -> return
        }

        print(USERNAME)

    }

    fun getUserName(){
        TUI.writeCorners("             ", top = true, left = true)
        TUI.writeCorners(NAMESCREENSTRING, top = true, left = true)
        TUI.writeChar(LETTER)
        TUI.positionCursor(1, NAMESCREENSTRING.length + USERNAME.length + 1)
        while(true){
            handleNameInputs()
        }
    }

    fun gameOverScreen(){
        TUI.clearScreen()
        TUI.writeCorners("Score: $SCORE", top = false, left = true)
        TUI.writeCorners("GAME OVER!", top = true, left = true)
        getUserName()
        NJOGOS += 1
    }

    fun handleGameInputs() {

        when (val key = TUI.waitAnyKey(GAMEKEYWAITTIME)) {
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
            handleGameInputs()
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