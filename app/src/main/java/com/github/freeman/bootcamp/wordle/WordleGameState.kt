package com.github.freeman.bootcamp.wordle

import java.io.File
import java.io.UncheckedIOException
import java.security.InvalidParameterException
import kotlin.random.Random
import java.lang.Character.MIN_VALUE as nullChar

/**
 * This is a wordle Game. The game is the same as a mastermind but with words and letters instead of colors.
 * Green means right position, yellow means right letter but wrong spot, red means wrong letter.
 * You have to try words until you guess the right word
 *
 * @param current_line the last row of letters that have been added to the game
 * @param grid The tiles containing the letters and the state of the letters
 * @param wordOnly true means you have to chose an existing english word and random letters are not allowed
 * @param wordToGuess the word to guess in order to wins the game
 * @param validWords the set of existing english words
 * @param solutions the set of existing words that the wordToGuess is randomly chosen from
 */
class WordleGameState private constructor(
    private val current_line: Int,
    private val grid: Array<Array<Tile>>,
    private val wordOnly: Boolean,
    private val wordToGuess: String,
    private val validWords: List<String>,
    private val solutions: List<String>
) {
    constructor(wordOnly: Boolean, solutions: List<String>, validWords: List<String>) : this(
        0,
        Array(8) { Array(5) { Tile(nullChar, TileState.EMPTY) } },
        wordOnly,
        solutions[Random.nextInt(0, solutions.size)],
        validWords.toList(),
        solutions
    )

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
    class Tile(var letter: Char, var state: TileState)

    /**
     * add a row with letter to the game and determine if it is the wordToGuess or not
     */
    fun submitWord(word: String): WordleGameState {
        if (word.length != 5) {
            throw InvalidParameterException("the submitted word does not contains 5 letters")
        }

        val grid = this.grid
        // accept only existing words if random letters mode is not activated
        if (wordOnly) {
            if (!validWords.contains(word)) {
                return this
            }
        }
        // the game is over if the grid is full, starts a new game
        if (current_line < grid.size) {
            // initiate row
            for (i in 0 until grid[0].size) {
                grid[current_line][i].letter = word[i]
            }
            // We need a way to be able to count letter only once
            // remainingLetters is used address letter in wrong spot correctly
            // For a specific letter we don't want to assign more wrong spot that the word contains
            // this letter
            var remainingLetters = wordToGuess
            val row = grid[current_line]
            for (i in 0 until grid[0].size) {
                val tile = row[i]

                if (tile.letter == remainingLetters[i]) {// right letter at right position
                    remainingLetters = remainingLetters.replaceFirst(tile.letter, ' ', true)
                    tile.state = TileState.CORRECT
                } else if (!remainingLetters.contains(tile.letter)) {// word does not contains the letter
                    tile.state = TileState.INCORRECT
                }
            }

            for (i in 0 until grid[0].size) {// we loop for wrong spot after so we don't counts the same letter twice
                val tile = row[i]
                if (tile.state != TileState.CORRECT && tile.state != TileState.INCORRECT) {
                    if (remainingLetters.contains(tile.letter)) {
                        tile.state = TileState.WRONG_SPOT
                        remainingLetters = remainingLetters.replaceFirst(tile.letter, ' ', true)
                    } else {
                        tile.state = TileState.INCORRECT
                    }
                }
            }

            return WordleGameState(
                current_line + 1,
                grid,
                wordOnly,
                wordToGuess,
                validWords,
                solutions
            )
        } else {
            return WordleGameState(wordOnly, solutions, validWords); // new Game
        }
    }

    /**
     * return a list containing all tile in the right order going from left to right then up to down
     */
    fun getTiles(): MutableList<WordleGameState.Tile> {
        return grid.flatten().toMutableList()
    }

    /**
     * return the grid containing all tile of the game
     */
    fun getGrid(): Array<Array<Tile>> {
        return grid.clone()
    }

    /**
     * manually set the wordToGuess for testing
     */
    fun withSetWordToGuess(word: String): WordleGameState {
        return WordleGameState(current_line, grid, wordOnly, word, validWords, solutions)
    }


}