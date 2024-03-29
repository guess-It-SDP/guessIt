package com.github.freeman.bootcamp.wordle

import com.github.freeman.bootcamp.games.wordle.WordleGameState
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.security.InvalidParameterException

private val NB_ROW = 8

class WordleGameStateTest {
    var helloWordle = WordleGameState.startGame(false, listOf("hello"), listOf("hello"), NB_ROW)

    @Test
    fun getTilesWorksWithEmptyTileGrid() {
        var tiles = helloWordle.getTiles()
        for (i in 0..5 * 8 - 1) {
            assertEquals('\u0000', tiles.get(i).letter)
            assertEquals(WordleGameState.TileState.EMPTY, tiles.get(i).state)
        }
    }

    @Test
    fun submitWordAndGetTilesWorksWithHellotoHello() {
        var wordle = helloWordle.withSubmittedWord("hello")
        var tiles = wordle.getTiles()
        assertEquals('h', tiles.get(0).letter)
        assertEquals('e', tiles.get(1).letter)
        assertEquals('l', tiles.get(2).letter)
        assertEquals('l', tiles.get(3).letter)
        assertEquals('o', tiles.get(4).letter)
        assertEquals('\u0000', tiles.get(5).letter)
        for (i in 0..4) {
            assertEquals(WordleGameState.TileState.CORRECT, tiles.get(i).state)
        }
    }

    @Test
    fun submitWordAndGetGridWorksWithHelloToHello() {
        var wordle = helloWordle.withSubmittedWord("hello")
        var grid = wordle.getGrid()
        assertEquals('h', grid[0][0].letter)
        assertEquals('e', grid[0][1].letter)
        assertEquals('l', grid[0][2].letter)
        assertEquals('l', grid[0][3].letter)
        assertEquals('o', grid[0][4].letter)
        assertEquals('\u0000', grid[1][0].letter)
        for (i in 0..4) {
            assertEquals(WordleGameState.TileState.CORRECT, grid[0][1].state)
        }
    }

    @Test
    fun submitWordAndGetGridWorksWith4WrongsWords() {
        val word1 = "ehxxo"
        val word2 = "llppl"
        val word3 = "lllll"
        val word4 = "lllol"
        var wordle = helloWordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word2)
        wordle = wordle.withSubmittedWord(word3)
        wordle = wordle.withSubmittedWord(word4)
        var grid = wordle.getGrid()
        assertEquals(WordleGameState.TileState.WRONG_SPOT, grid[0][0].state)
        assertEquals(WordleGameState.TileState.WRONG_SPOT, grid[0][1].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[0][2].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[0][3].state)
        assertEquals(WordleGameState.TileState.CORRECT, grid[0][4].state)
        assertEquals(WordleGameState.TileState.WRONG_SPOT, grid[1][0].state)
        assertEquals(WordleGameState.TileState.WRONG_SPOT, grid[1][1].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[1][2].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[1][3].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[1][4].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[2][0].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[2][1].state)
        assertEquals(WordleGameState.TileState.CORRECT, grid[2][2].state)
        assertEquals(WordleGameState.TileState.CORRECT, grid[2][3].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[2][4].state)
        assertEquals(WordleGameState.TileState.WRONG_SPOT, grid[3][0].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[3][1].state)
        assertEquals(WordleGameState.TileState.CORRECT, grid[3][2].state)
        assertEquals(WordleGameState.TileState.WRONG_SPOT, grid[3][3].state)
        assertEquals(WordleGameState.TileState.INCORRECT, grid[3][4].state)
        for (i in 0..4) {
            assertEquals(word1[i], grid[0][i].letter)
            assertEquals(word2[i], grid[1][i].letter)
            assertEquals(word3[i], grid[2][i].letter)
            assertEquals(word4[i], grid[3][i].letter)
        }
        assertEquals('\u0000', grid[4][0].letter)
    }

    @Test
    fun submit9WrongWordsRestartsTheGame() {
        val word1 = "ehxxo"
        var wordle = helloWordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word1)
        wordle = wordle.withSubmittedWord(word1)

        var tiles = wordle.getTiles()
        for (i in 0..5 * 8 - 1) {
            assertEquals('\u0000', tiles.get(i).letter)
            assertEquals(WordleGameState.TileState.EMPTY, tiles.get(i).state)
        }
    }

    @Test
    fun withSetWordToGuessReturnNewGameWithCorrectWord() {
        val word1 = "train"
        var wordle = helloWordle.withSetWordToGuess(word1)
        wordle.withSubmittedWord("train")
        for (i in 0..4) {
            assertEquals(word1[i], wordle.withSubmittedWord(word1).getGrid()[0][i].letter)
            assertEquals(WordleGameState.TileState.CORRECT, wordle.withSubmittedWord(word1).getGrid()[0][i].state)
        }

    }
    @Test
    fun wordsOnlyRefusesRandomLetter(){
        var wordle = WordleGameState.startGame(true,listOf("hello"),listOf("hello"), NB_ROW )
        wordle = wordle.withSubmittedWord("ebcde")
        assertEquals('\u0000',wordle.getTiles().get(0).letter)
    }

    @Test(expected = InvalidParameterException::class)
    fun submit6LettersThrowIllegalArgumentEx(){
        var wordle = WordleGameState.startGame(true,listOf("hello"),listOf("hello"), NB_ROW ).withSubmittedWord("abcdef")
    }

}