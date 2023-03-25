package com.github.freeman.bootcamp.wordle

import java.io.File
import java.io.UncheckedIOException
import java.security.InvalidParameterException
import kotlin.random.Random
import java.lang.Character.MIN_VALUE as nullChar

class WordleGameState private constructor(
    private val current_line: Int,
    private val grid: Array<Array<Tile>>,
    private val wordOnly: Boolean,
    private var wordToGuess: String,
    private val validWords: List<String>
) {
    // private var wordToGuess = solutions[Random.nextInt(0, solutions.size)]
    constructor(wordOnly: Boolean, solutions: List<String>, validWords: List<String>) : this(
        0,
        Array(8) { Array(5) { Tile(nullChar, TileState.EMPTY) } },
        wordOnly,
        solutions[Random.nextInt(0, solutions.size)],
        validWords.toList()
    )

    enum class TileState(val rgb: Long) {
        EMPTY(0xFF000000), CORRECT(0xFF00FF00), WRONG_SPOT(0xFFFFFF00), INCORRECT(0xFFFF0000)
    }

    class Tile(var letter: Char, var state: TileState)

    fun submitWord(word: String): WordleGameState {
        val grid = this.grid
        // accept only existing words if random letters mode is not activated
        if (wordOnly) {
            if (!validWords.contains(word)) {
                throw InvalidParameterException()
            }
        }
        // the game is over if we can not go after the grid is full
        if (current_line < grid.size) {
            // initiate row
            for (i in 0..grid[0].size-1) {
                grid[current_line][i].letter = word[i]
            }
        // We need a way to be able to count letter only once
        // remaining is used address letter in wrong spot correctly
        // For a specific letter we don't want to assign more wrong spot
        // that the word contains this letter
        var remaining = wordToGuess
        val row = grid[current_line]
        for (i in 0..grid[0].size-1) {
            val tile = row[i]
            if (tile.letter == remaining[i]) {
                remaining = remaining.replaceFirst(tile.letter, ' ', true)
                tile.state = TileState.CORRECT
            } else if (!remaining.contains(tile.letter)) {
                tile.state = TileState.INCORRECT
            }

            for (i in 0..grid[0].size-1) {
                if (tile.state != TileState.CORRECT && tile.state != TileState.INCORRECT) {
                    if (remaining.contains(tile.letter)) {
                        tile.state = TileState.WRONG_SPOT
                        remaining = remaining.replaceFirst(tile.letter, ' ', true)
                    } else {
                        tile.state = TileState.WRONG_SPOT
                    }
                }
            }
        }
            return WordleGameState(current_line+1, grid, wordOnly, wordToGuess, validWords)
        } else {
            return this;
        }
    }
    public fun getTiles(): MutableList<WordleGameState.Tile> {
        val tiles: MutableList<WordleGameState.Tile> = ArrayList()
        grid.forEach { row -> tiles.addAll(row.toList()) }
        return tiles
    }

    companion object {
        /**
         * Create a list containing each lines of a file
         */
        private fun load(fileName: String): List<String> = listOf("")
        //   File(fileName).readLines()

        //  private val solutions = load("wordle_common.txt")
        //  private val validWords = load("wordle_all.txt")
        /*
        private fun getRandomWord(): String {
            return solutions[Random.nextInt(0, solutions.size)]
        }

         */
    }

    fun getGrid(): Array<Array<Tile>> {
        return grid.clone()
    }
    fun setWordToGuess(word: String){
        wordToGuess=word
    }

}