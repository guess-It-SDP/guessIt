package com.github.freeman.bootcamp.games.wordle

import java.security.InvalidParameterException
import kotlin.random.Random
import java.lang.Character.MIN_VALUE as NULLCHAR

/**
 * This is a wordle Game. The game is the same as a mastermind but with words and letters instead of colors.
 * Green means right position, yellow means right letter but wrong spot, red means wrong letter.
 * You have to try words until you guess the right word
 *
 * @param currentLine the last row of letters that have been added to the game
 * @param grid The tiles containing the letters and the state of the letters
 * @param wordOnly true means you have to chose an existing english word and random letters are not allowed
 * @param wordToGuess the word to guess in order to wins the game
 * @param validWords the set of existing english words
 * @param solutions the set of existing words that the wordToGuess is randomly chosen from
 */
 class WordleGameState {
        private val currentLine: Int
        private val grid: Array<Array<Tile>>
        private val wordOnly: Boolean
        private val wordToGuess: String
        private val validWords: List<String>
        private val solutions: List<String>

    private constructor(
        currentLine: Int,
       grid: Array<Array<Tile>>,
       wordOnly: Boolean,
        wordToGuess: String,
       validWords: List<String>,
        solutions: List<String>,
    ){
        this.currentLine = currentLine
        val generatedArray = Array(grid.size) { i-> Array(grid[0].size) { j->Tile(grid[i][j].letter, grid[i][j].state) } }
        this.grid = generatedArray
        this.wordOnly = wordOnly
        this.wordToGuess = wordToGuess
        this.validWords = validWords.toList()
        this.solutions = solutions.toList()
    }


    class Builder {
        private var currentLine = 0
        private lateinit var grid: Array<Array<Tile>>
        private var wordOnly = false
        private lateinit var wordToGuess: String
        private lateinit var validWords: List<String>
        private lateinit var solutions: List<String>

        fun currentLine(currentLine: Int) = apply { this.currentLine = currentLine }
        fun grid(grid: Array<Array<Tile>>) = apply { this.grid = grid }
        fun wordOnly(wordOnly: Boolean) = apply { this.wordOnly = wordOnly }
        fun wordToGuess(wordToGuess: String) = apply { this.wordToGuess = wordToGuess }
        fun validWords(validWords: List<String>) = apply { this.validWords = validWords }
        fun solutions(solutions: List<String>) = apply { this.solutions = solutions }

        // Build the WordleGameState instance with the given parameters
        fun build() = WordleGameState(
            currentLine = currentLine,
            grid = grid,
            wordOnly = wordOnly,
            wordToGuess = wordToGuess,
            validWords = validWords,
            solutions = solutions
        )
    }

    companion object {
        /**
         * This constructor is used to start a game, chose a random word to guess among solutions
         *
         * @param wordOnly is random letter submission is allowed
         * @param solutions the set of existing words that the wordToGuess is randomly chosen from
         * @param validWords the set of existing english words
         */
        fun startGame(
            wordOnly: Boolean,
            solutions: List<String>,
            validWords: List<String>,
            nbRow: Int, ) = WordleGameState(
            0,
            Array(nbRow) { Array(5) { Tile(NULLCHAR, TileState.EMPTY) } },
            wordOnly,
            solutions[Random.nextInt(0, solutions.size)],
            validWords.toList(),
            solutions
        )
    }

/**
     * The state of a tile
     * @param argb contains an advised color, you don't have to keep this one for the graphic interface
     * EMPTY no letter is added, it should contains the null character
     * CORRECT the word contains that letter and the letter is at the right position
     * WRONG_SPOT the word contains the letter but at a different position
     * INCORRECT the word does not contains the letter
     */
    enum class TileState(val argb: Long) {
        EMPTY(0xFF000000), CORRECT(0xFF00FF00), WRONG_SPOT(0xFFFFFF00), INCORRECT(0xFFFF0000)
    }

    /**
     * the tile containing a letter and if the wordToGuess contains that letter at the right position or not
     *
     * @param Char the letter
     * @param state if that letter at the right position or not
     */
    class Tile( val letter: Char,  val state: TileState) {
        companion object {
            fun builder(): Builder {
                return Builder()
            }
        }

        class Builder {
            private var letter: Char = NULLCHAR
            private var state: TileState = TileState.EMPTY

            fun letter(letter: Char): Builder {
                this.letter = letter
                return this
            }

            fun state(state: TileState): Builder {
                this.state = state
                return this
            }

            fun build(): Tile {
                return Tile(letter, state)
            }
        }
    }

    /**
     * return a list containing all tile in the right order going from left to right then up to down
     */
    fun getTiles(): MutableList<Tile> {
        return getGrid().flatten().toMutableList()
    }

    /**
     * manually set the wordToGuess for testing
     */
    fun withSetWordToGuess(word: String): WordleGameState {
        return WordleGameState(currentLine, grid, wordOnly, word, validWords, solutions)
    }

    /**
     * add a row with letter to the game and determine if it is the wordToGuess or not
     * color the tiles corresponding the respective state the player knows which letter are
     * contained by the word and at which place
     *
     *@param submittedWord the word to compare with the hidden word
     *@throws InvalidParameterException
     */
    fun withSubmittedWord(submittedWord: String): WordleGameState {
        checkArgumentLength(submittedWord)
        // accept only existing words if random letters mode is not activated
        if (wordOnly) {
            if (!validWords.contains(submittedWord)) {
                return deepCopy()
            }
        }
        // the game is over if the grid is full, starts a new game
        if (currentLine >= grid.size) {
            return startGame(wordOnly, solutions, validWords,grid.size) // new Game
        } else {

            // We need a way to be able to count letter only once
            // remainingLetters is used address letter in wrong spot correctly
            // For a specific letter we don't want to assign more wrong spot that the word contains
            // this letter
            var remainingLetters = wordToGuess
            val grid = getGrid()
            val row = grid[currentLine]

            initiateRow(row, submittedWord)

            remainingLetters = removeCorrectLetters(remainingLetters, row)

            removeWrongSpotLetters(remainingLetters, row)

            return WordleGameState(
                currentLine + 1, grid, wordOnly, wordToGuess, validWords, solutions
            )
        }
    }

    /**
     * check that only 5 letters are submitted
     * @param submittedWord
     * @throws InvalidParameterException
     */
    private fun checkArgumentLength(submittedWord: String) {
        if (submittedWord.length != 5) {
            throw InvalidParameterException("the submitted word does not contains 5 letters")
        }
    }

    /**
     * add the word submitted to the row for checking which letter is a the right place
     * @param rom line used to express the state of the letter submitted to the player
     * @param submittedWord the word committed by the player to try to guess the wordToGuess
     */
    private fun initiateRow(row: Array<Tile>, submittedWord: String) {
        for (i in 0 until grid[0].size) {
            row[i] = Tile.builder().letter(submittedWord[i]).state(row[i].state).build()
        }
    }

    /**
     * Removes the correct present at the right place and color the tile in green if it is the
     * right letter and color the tile in red if the wordToGuess does not contains the letter
     *
     * @param remainingLetters waiting letters to be compared with submitted words
     * @param row line used to express the state of the letter submitted to the player
     */
    private fun removeCorrectLetters(remainingLetters: String, row: Array<Tile>): String {
        var remainingLetters = remainingLetters
        for (i in 0 until grid[0].size) {
            val tile = row[i]
            val tileB = Tile.builder().letter(row[i].letter).state(row[i].state)
            if (tile.letter == remainingLetters[i]) {// right letter at right position
                remainingLetters = remainingLetters.replaceFirst(tile.letter, ' ', true)
                tileB.state(TileState.CORRECT)
            } else if (!remainingLetters.contains(tile.letter)) {// word does not contains the letter
                tileB.state(TileState.INCORRECT)
            }
            row[i]=tileB.build()
        }

        return remainingLetters
    }

    /**
     * Removes the letters contained by the word but at the wrong place, counts extra letter as
     * incorrect and color the tile in red if the wordToGuess does not contains the letter
     *
     * @param remainingLetters waiting letters to be compared with submitted words
     * @param row line used to express the state of the letter submitted to the player
     */
    private fun removeWrongSpotLetters(remainingLetters: String, row: Array<Tile>): String {
        var remainingLetters = remainingLetters
        for (i in 0 until grid[0].size) {// we loop for wrong spot after so we don't counts the same letter twice
            val tile = row[i]
            val tileB = Tile.builder().letter(row[i].letter).state(row[i].state)
            if (tile.state != TileState.CORRECT && tile.state != TileState.INCORRECT) {
                if (remainingLetters.contains(tile.letter)) {
                    tileB.state(TileState.WRONG_SPOT)
                    remainingLetters = remainingLetters.replaceFirst(tile.letter, ' ', true)
                } else {
                    tileB.state(TileState.INCORRECT)
                }
            }
            row[i]=tileB.build()
        }
        return remainingLetters
    }

    /**
     * The tiles of the games
     */
    fun getGrid(): Array<Array<Tile>>{
        val generatedArray = Array(grid.size) { i-> Array(grid[0].size) { j->Tile(grid[i][j].letter, grid[i][j].state) } }
        return generatedArray
    }


     private fun  deepCopy(): WordleGameState{
         return WordleGameState(
             currentLine = currentLine,
             grid = grid,
             wordOnly = wordOnly,
             wordToGuess = wordToGuess,
             validWords = validWords,
             solutions = solutions
         )
     }
}