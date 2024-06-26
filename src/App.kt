import isel.leic.utils.Time
import kotlin.system.exitProcess

object App{

/******************************* Variáveis e constantes globais *******************************/

    // Variáveis de jogo
    private var GAMERUN: Boolean = false
    private var INSERTINGNAME = false
    private var SCORE: Int = 0
    private var SCORELIST = ArrayList<Scores.Score>()
    private const val POINTSPERKILL = 2
    private var PREVCOINS = 0
    private var CREDITS = 0
    private const val CREDITSTOPLAY = 1
    private const val CREDITSPERCOIN = 2
    private var NGAMES = 0

    // Variáveis de mira
    private var AIMPOS = 1
    private const val INITAIMCOL = 2
    private var AIMVALUE: Char? = null
    private const val AIMSTRING = "[ "

    // Variáveis de inimigos
    private var SPAWNTIME = 0
    private const val DEFAULTSPAWNTIME = 1000
    private const val INCREASEDIF = 1
    private var TOPINVADERS = ""
    private var BOTTOMIMVADERS = ""

    // Constantes de tempo
    private const val STARTKEYWAITTIME: Long = 50
    private const val GAMEKEYWAITTIME: Long = 50
    private const val ANIMATIONTIME = 500
    private const val IDLETIMER = 10000
    private const val PODIUMTIME = 4000
    private const val CHECKCOINTIME = 100
    private const val GAMEOVERTIME: Long = 2000

    // Variáveis e constantes da introdução do username
    private const val NAMESCREENSTRING = "Nome: "
    private var LETTER = ' '
    private var USERNAME = ""
    private const val USERNAMEMAXSIZE = 8

    // Variáveis manutenção
    private var TEST = false
    private var INM = false
    private const val MAITENANCEINPUTTIME: Long = 200

    // Variáveis de estatíticas
    private const val NGAMESSTRING = "Jogos: "
    private const val NCOINSSTRING = "Moedas: "

    // Variáveis ecrã principal
    private const val GAMETITLE = "Space Invaders"
    private var CREDITSSTRING = "Creditos "
    private var IDLE = false

    // Linhas do LCD
    private const val LCDTOPLINE = 1
    private const val LCDBOTTOMLINE = 2

/******************************* Teclas *******************************/

    // Teclas Jogo
    private const val BGAMESTART = '#'
    private const val BSHOOT = '#'
    private const val BCHANGEAIM = '*'

    // Teclas introdução do nome
    private const val BCYCLELETTERUP = '2'
    private const val BCYCLELETTERDOWN = '8'
    private const val BERASELETTER = '4'
    private const val BCONFIRMLETTER = '#'
    private const val BCONFIRMNAME = '*'

    // Teclas manutenção
    private const val BSYSTEMOFF = '0'
    private const val BCONFIRMSYSOFF = '0'
    private const val BDENYSYSOFF = '*'
    private const val BSTATISTICS = '#'
    private const val BRESETSTATISTICS = '*'
    private const val BTESTGAME = '*'


    // Inicia a classe App
    fun init(){
        Scores.init()
        TUI.init()
        ScoreDisplay.init()
        CoinAcceptor.init()

        PREVCOINS = Statistics.readCoins()
        NGAMES = Statistics.readNumberOfGames()
        USERNAME = ""
        CREDITS = 0
        SCORELIST = Scores.getScores()

        resetGameState()
    }

    // Inicia as variáveis de jogo para começar um jogo novo
    private fun resetGameState(){
        TOPINVADERS = ""
        BOTTOMIMVADERS = ""
        USERNAME = ""
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
    private fun waitGameStart(): Boolean = TUI.waitSpecificKey(BGAMESTART, STARTKEYWAITTIME)

    // Trata dos inputs do ecrã inicial
    private fun initScreen(){

        var animationTime = System.currentTimeMillis()
        var idleTime = System.currentTimeMillis()
        var scoreTimer = System.currentTimeMillis()
        var scoreIndex = 0
        var animationIndex = 0

        while(true){
            if(waitGameStart() && CREDITS >= CREDITSTOPLAY){
                prepGameScreen()
                break
            }
            if(CoinAcceptor.checkCoin() && System.currentTimeMillis() - idleTime > CHECKCOINTIME){
                IDLE = false
                idleTime = System.currentTimeMillis()
                PREVCOINS += 1
                CREDITS += CREDITSPERCOIN
                TUI.clearLine(LCDBOTTOMLINE)
                TUI.writeCorners("$CREDITSSTRING$CREDITS$", top = false, left = false)
            }
            if(System.currentTimeMillis() - idleTime > IDLETIMER){
                IDLE = true
            }
            if(IDLE && System.currentTimeMillis() - scoreTimer > PODIUMTIME && SCORELIST.isNotEmpty()){

                TUI.clearLine(LCDBOTTOMLINE)

                val score = SCORELIST[scoreIndex]

                TUI.writeCorners("${scoreIndex+1}.${score.name}", top = false, left = true)
                TUI.writeCorners( "${score.points}", top = false, left = false)
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
                ScoreDisplay.animation(animationIndex)
                animationTime = System.currentTimeMillis()
                animationIndex++
                if(animationIndex > ScoreDisplay.ANIMATIONFRAMES){
                    animationIndex = 0
                }
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
        TUI.positionCursor(AIMPOS, INITAIMCOL)
    }

    // Verifica se o jogador perdeu o jogo
    private fun checkGameOver(): Boolean {
        if(TOPINVADERS.length > 14 || BOTTOMIMVADERS.length > 14){
            GAMERUN = false
            return true
        }
        return false
    }

    private fun writeStatisticsToScreen(){
        TUI.clearScreen()
        TUI.writeCorners(NGAMESSTRING + NGAMES, top = true, left = true)
        TUI.writeCorners(NCOINSSTRING + PREVCOINS, top = false, left = true)
        TUI.cursorOutOfScreen()
    }

    // Verifica se o utilizador pretende colocar as estatísticas a zero, voltar para o menu de manutenção ou o inicial
    private fun handleStatsReset() {
        while (true) {
            when (TUI.waitAnyKey(MAITENANCEINPUTTIME)) {
                BRESETSTATISTICS -> {
                    CoinAcceptor.resetCoins()
                    PREVCOINS = 0
                    NGAMES = 0
                    writeStatisticsToScreen()

                }
                BSTATISTICS -> {
                    handleMaintenanceInputs()
                }
            }

            if(!M.checkMaintenance()){
                TEST = false
                resetGameState()
            }
        }
    }

    // Verifica os inputs no menu de manutenção
    private fun handleMaintenanceInputs() {

        TUI.clearScreen()
        TUI.writeCorners("$BTESTGAME-Testar jogo", top = true, left = true)
        TUI.writeCorners("$BSTATISTICS-Stats $BSYSTEMOFF-OFF", top = false, left = false)
        TUI.cursorOutOfScreen()

        while (INM) {
            when (TUI.waitAnyKey(MAITENANCEINPUTTIME)) {
                BTESTGAME -> {
                    prepGameScreen()
                }

                BSTATISTICS -> {
                    writeStatisticsToScreen()
                    handleStatsReset()
                }
                BSYSTEMOFF -> {
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
        TUI.writeCorners("$BCONFIRMSYSOFF - Sim  $BDENYSYSOFF - Nao", top = false, left = false)
        TUI.cursorOutOfScreen()

        while(true) {
            when (TUI.waitAnyKey(MAITENANCEINPUTTIME)) {
                BCONFIRMSYSOFF -> {
                    break
                }

                BDENYSYSOFF -> {
                    handleMaintenanceInputs()
                }
            }
        }

        Statistics.saveStatsFile(PREVCOINS, NGAMES)
        Scores.writeScoresToFile(Scores.getScores())
        TUI.screenOff()
        exitProcess(1)
    }

    // Inputs de introdução do nome de utilizador após o jogo
    private fun handleNameInputs(){
        when (TUI.waitAnyKey(GAMEKEYWAITTIME)) {
            BCYCLELETTERUP -> {
                if(LETTER >= 'Z' || LETTER == ' '){
                    LETTER = 'A'-1
                }
                LETTER++
                TUI.writeChar(LETTER)
                TUI.positionCursor(LCDTOPLINE, NAMESCREENSTRING.length + USERNAME.length + 1)
            }
            BCYCLELETTERDOWN -> {
                if(LETTER <= 'A'){
                    LETTER = 'Z'+1
                }
                LETTER--
                TUI.writeChar(LETTER)
                TUI.positionCursor(LCDTOPLINE, NAMESCREENSTRING.length + USERNAME.length + 1)
            }
            BERASELETTER -> {
                if(USERNAME.isNotEmpty()) {
                    USERNAME = USERNAME.dropLast(1)
                    TUI.positionCursor(LCDTOPLINE, NAMESCREENSTRING.length + USERNAME.length + 1)
                    LETTER = ' '
                    TUI.writeString("  ")
                    TUI.positionCursor(LCDTOPLINE, NAMESCREENSTRING.length + USERNAME.length + 1)
                }
            }
            BCONFIRMLETTER -> {
                if(LETTER in 'A'..'Z'){
                    USERNAME += LETTER
                    TUI.positionCursor(LCDTOPLINE, NAMESCREENSTRING.length + USERNAME.length + 1)
                    LETTER = ' '
                    TUI.writeChar(LETTER)
                    TUI.positionCursor(LCDTOPLINE, NAMESCREENSTRING.length + USERNAME.length + 1)
                }
            }
            BCONFIRMNAME -> {
                if(USERNAME.isNotEmpty())
                    INSERTINGNAME = false
            }

            else -> return
        }

        if(USERNAME.length == USERNAMEMAXSIZE){
            INSERTINGNAME = false
        }

    }

    // Prepara o menu de introdução do nome de utilizador
    private fun getUserName(){
        INSERTINGNAME = true
        TUI.clearLine(LCDTOPLINE)
        TUI.writeCorners(NAMESCREENSTRING, top = true, left = true)
        TUI.writeChar(LETTER)
        TUI.positionCursor(LCDTOPLINE, NAMESCREENSTRING.length + USERNAME.length + 1)

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
        TUI.cursorOutOfScreen()
        Thread.sleep(GAMEOVERTIME)
        getUserName()
    }

    // Inputs durante o jogo
    private fun handleGameInputs() {

        when (val key = TUI.waitAnyKey(GAMEKEYWAITTIME)) {
            BCHANGEAIM -> {
                if (LCD.getCursorLine() == LCDTOPLINE) {
                    resetAim()
                    TUI.positionCursor(LCDBOTTOMLINE, INITAIMCOL)
                    AIMPOS = LCDBOTTOMLINE
                }
                else if (LCD.getCursorLine() == LCDBOTTOMLINE) {
                    resetAim()
                    TUI.positionCursor(LCDTOPLINE, INITAIMCOL)
                    AIMPOS = LCDTOPLINE
                }
            }

            BSHOOT -> {processAim()}

            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                TUI.writeChar(key)
                TUI.positionCursor(AIMPOS, INITAIMCOL)
                AIMVALUE = key
            }
            else -> return
        }

    }

    // Coloca a mira na posição correta
    private fun resetAim(){
        TUI.positionCursor(AIMPOS, INITAIMCOL)
        TUI.writeChar(' ')
        TUI.positionCursor(AIMPOS, INITAIMCOL)
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
            CREDITS -= 1
            gameOverScreen()
        }

        resetGameState()
    }
}

fun main(){
    App.init()
}