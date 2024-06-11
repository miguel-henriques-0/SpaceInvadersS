import isel.leic.utils.Time
import kotlin.system.exitProcess

object App{

    // Variáveis de jogo
    private var GAMERUN: Boolean = false
    private var INSERTINGNAME = false
    private var SCORE: Int = 0
    private var SCORELIST = ArrayList<Scores.Score>()
    private const val POINTSPERKILL = 2
    private var PREVCOINS = 0
    private var CREDITS = 0
    private var NGAMES = 0

    // Variáveis de mira
    private var AIMPOS = 1
    private var AIMVALUE: Char? = null
    private const val AIMSTRING = "[ "

    // Variáveis de inimigos
    private var SPAWNTIME = 0
    private const val DEFAULTSPAWNTIME = 1000
    private const val INCREASEDIF = 1
    private var TOPINVADERS = ""
    private var BOTTOMIMVADERS = ""

    // Constantes de tempo
    private const val STARTKEYWAITTIME: Long = 100
    private const val GAMEKEYWAITTIME: Long = 100
    private const val ANIMATIONTIME = 1000
    private const val IDLETIMER = 10000
    private const val PODIUMTIME = 4000
    private const val CHECKCOINTIME = 100
    private const val GAMEOVERTIME: Long = 2000

    // Variáveis e constantes da introdução do username
    private const val NAMESCREENSTRING = "Nome: "
    private var LETTER = 'A'
    private var USERNAME = ""

    // Variáveis manutenção
    private var TEST = false
    private var INM = false
    private const val MAITENANCEINPUTTIME: Long = 200

    // Variáveis ecrã principal
    private const val GAMETITLE = "Space Invaders"
    private var CREDITSSTRING = "Creditos "
    private var IDLE = false


    // Inicia a classe App
    fun init(){
        Scores.init()
        TUI.init()
        ScoreDisplay.init()
        CoinAcceptor.init()

        PREVCOINS = Statistics.readCoins()
        NGAMES = Statistics.readNumberOfGames()
        CREDITS = 0
        SCORELIST = Scores.getScores()

        resetGameState()
    }

    // Inicia as variáveis de jogo para começar um jogo novo
    private fun resetGameState(){
        TOPINVADERS = ""
        BOTTOMIMVADERS = ""
        SCORE = 0
        AIMPOS = 1
        AIMVALUE = null
        SPAWNTIME = DEFAULTSPAWNTIME
        TUI.clearScreen()
        TUI.writeCorners(GAMETITLE, top = true, left = true)
        TUI.writeCorners("$CREDITSSTRING$CREDITS$", top = false, left = false)
        initScreen()
    }

    // Aumenta os pontos do jogador
    private fun scoreUp(value: Int){
        SCORE += value
        ScoreDisplay.setScore(SCORE, false)
    }

    // Espera pela tecla que dá início ao jogo
    private fun waitGameStart(): Boolean = TUI.waitSpecificKey('#', STARTKEYWAITTIME)

    // Trata dos inputs do ecrã inicial
    private fun initScreen(){

        var animationTime = System.currentTimeMillis()
        var idleTime = System.currentTimeMillis()
        var scoreTimer = System.currentTimeMillis()
        var scoreIndex = 0

        while(true){
            if(waitGameStart() && CREDITS >= 2){
                prepGameScreen()
                break
            }
            if(CoinAcceptor.checkCoin() && System.currentTimeMillis() - idleTime > CHECKCOINTIME){
                IDLE = false
                idleTime = System.currentTimeMillis()
                CREDITS += 2
                TUI.clearLine(2)
                TUI.writeCorners("$CREDITSSTRING$CREDITS$", top = false, left = false)
            }
            if(System.currentTimeMillis() - idleTime > IDLETIMER){
                IDLE = true
            }
            if(IDLE && System.currentTimeMillis() - scoreTimer > PODIUMTIME && SCORELIST.isNotEmpty()){

                TUI.clearLine(2)

                val score = SCORELIST[scoreIndex]

                TUI.writeCorners("${scoreIndex+1}. ${score.name} ${score.points}", top = false, left = true)
                TUI.cursorOutOfScreen()

                scoreIndex++
                if(scoreIndex > SCORELIST.size-1){
                    scoreIndex = 0
                }

                scoreTimer = System.currentTimeMillis()

            }
            if(M.checkMaintenance()){
                IDLE = false
                TEST = true
                INM = true
                break
            }
            if(System.currentTimeMillis() - animationTime > ANIMATIONTIME){
                ScoreDisplay.animation()
                animationTime = System.currentTimeMillis()
            }
        }

        handleMaintenanceInputs()

    }

    // Prepara o ecrã para o jogo
    private fun prepGameScreen(){
        TUI.clearScreen()
        ScoreDisplay.setScore(0, false)
        TUI.writeCorners(AIMSTRING, top = false, left = true)
        TUI.writeCorners(AIMSTRING, top = true, left = true)
        resetAim()
        startGame()
    }

    // Insere invasores de forma aleatória numa das linhas
    private fun spawnInvaders(){

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

    // Verifica se o jogador perdeu o jogo
    private fun checkGameOver(): Boolean {
        if(TOPINVADERS.length > 14 || BOTTOMIMVADERS.length > 14){
            GAMERUN = false
            return true
        }
        return false
    }

    // Verifica se o utilizador pretende colocar as estatísticas a zero, voltar para o menu de manutenção ou o inicial
    private fun handleStatsReset() {
        while (true) {
            when (TUI.waitAnyKey(MAITENANCEINPUTTIME)) {
                '*' -> {
                    CoinAcceptor.resetCoins()
                    PREVCOINS = 0
                    NGAMES = 0
                    TUI.writeCorners(Statistics.GAMESSTRING + NGAMES, top = true, left = true)
                    TUI.writeCorners(Statistics.COINSSTRING + PREVCOINS, top = false, left = true)
                }
                '#' -> {
                    handleMaintenanceInputs()
                }
            }

            if(!M.checkMaintenance()){
                initScreen()
            }
        }
    }

    // Verifica os inputs no menu de manutenção
    private fun handleMaintenanceInputs() {

        TUI.clearScreen()
        TUI.writeCorners("*-Testar jogo", top = true, left = true)
        TUI.writeCorners("#-Stats 0-OFF", top = false, left = false)
        TUI.cursorOutOfScreen()

        while (INM) {
            when (TUI.waitAnyKey(MAITENANCEINPUTTIME)) {
                '*' -> {
                    prepGameScreen()
                }

                '#' -> {
                    TUI.clearScreen()
                    PREVCOINS = CoinAcceptor.getCoins()
                    TUI.writeCorners("NJOGOS: $NGAMES", top = true, left = true)
                    TUI.writeCorners("MOEDAS: $PREVCOINS", top = false, left = true)
                    TUI.cursorOutOfScreen()
                    handleStatsReset()
                }
                '0' -> {
                    confirmSystemShutdown()
                }
            }

            if(!M.checkMaintenance()){
                IDLE = false
                INM = false
                TEST = false
                break
            }
        }

        resetGameState()
    }

    // Confirma com o utilizador o encerramento do sistema
    // Em caso positivo guarda os scores e estatísticas nos ficheiros respetivos
    private fun confirmSystemShutdown(){

        TUI.clearScreen()
        TUI.writeCorners("Desligar sistema?", top = true, left = true)
        TUI.writeCorners("0 - Sim  * - Nao", top = false, left = false)
        TUI.cursorOutOfScreen()

        while(true) {
            when (TUI.waitAnyKey(MAITENANCEINPUTTIME)) {
                '0' -> {
                    break
                }

                '*' -> {
                    handleMaintenanceInputs()
                }
            }
        }

        Statistics.saveStatsFile(PREVCOINS, NGAMES)
        Scores.writeScoresToFile(Scores.getScores())
        exitProcess(1)
    }


    // Inputs de introdução do nome de utilizador após o jogo
    private fun handleNameInputs(){
        when (TUI.waitAnyKey(GAMEKEYWAITTIME)) {
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
                TUI.positionCursor(1, NAMESCREENSTRING.length + USERNAME.length + 1)
                LETTER = 'A'
                TUI.writeChar(LETTER)
                TUI.positionCursor(1, NAMESCREENSTRING.length + USERNAME.length + 1)
            }
            '*' -> {
                if(USERNAME.isNotEmpty())
                    INSERTINGNAME = false
            }

            else -> return
        }

        if(USERNAME.length == 8){
            INSERTINGNAME = false
        }

    }

    // Prepara o menu de introdução do nome de utilizador
    private fun getUserName(){
        INSERTINGNAME = true
        TUI.clearLine(1)
        TUI.writeCorners(NAMESCREENSTRING, top = true, left = true)
        TUI.writeChar(LETTER)
        TUI.positionCursor(1, NAMESCREENSTRING.length + USERNAME.length + 1)

        while(INSERTINGNAME){
            handleNameInputs()
        }


        val gamePlayed = Scores.Score(USERNAME, SCORE)
        Scores.addScore(gamePlayed)
        resetGameState()

    }

    // Prepara o ecrã de fim de jogo
    private fun gameOverScreen(){
        TUI.clearScreen()
        TUI.writeCorners("Score: $SCORE", top = false, left = true)
        TUI.writeCorners("GAME OVER!", top = true, left = true)
        Thread.sleep(GAMEOVERTIME)
        getUserName()
    }

    // Inputs durante o jogo
    private fun handleGameInputs() {

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

            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                TUI.writeChar(key)
                TUI.positionCursor(AIMPOS, 2)
                AIMVALUE = key
            }
            else -> return
        }

    }

    // Coloca a mira na posição correta
    private fun resetAim(){
        TUI.positionCursor(AIMPOS, 2)
        TUI.writeChar(' ')
        TUI.positionCursor(AIMPOS, 2)
        AIMVALUE = null
    }

    // Verifica se os invasores são abatidos
    private fun processAim() {
        when (AIMPOS) {
            1 -> {
                if (TOPINVADERS.isNotEmpty() && TOPINVADERS[0] == AIMVALUE) {
                    TOPINVADERS = TOPINVADERS.drop(1)
                    TUI.writeCorners(" $TOPINVADERS", top = true, left = false)
                    scoreUp(POINTSPERKILL)
                    resetAim()
                    SPAWNTIME -= INCREASEDIF
                }
            }

            2 -> {
                if (BOTTOMIMVADERS.isNotEmpty() && BOTTOMIMVADERS[0] == AIMVALUE) {
                    BOTTOMIMVADERS = BOTTOMIMVADERS.drop(1)
                    TUI.writeCorners(" $BOTTOMIMVADERS", top = false, left = false)
                    scoreUp(POINTSPERKILL)
                    resetAim()
                    SPAWNTIME -= INCREASEDIF
                }
            }
        }
    }

    // Inicia o jogo
    private fun startGame() {
        GAMERUN = true
        var lastSpawnTime = Time.getTimeInMillis()

        while (GAMERUN) {
            handleGameInputs()
            val currTime = Time.getTimeInMillis()
            if (currTime - lastSpawnTime > SPAWNTIME) {
                spawnInvaders()
                lastSpawnTime = currTime
            }
            if(checkGameOver())
                break
        }

        if(!TEST){
            NGAMES += 1
            CREDITS -= 2
            gameOverScreen()
        }

        resetGameState()
    }
}

fun main(){
    App.init()
}