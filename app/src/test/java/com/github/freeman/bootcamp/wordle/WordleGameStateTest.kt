package com.github.freeman.bootcamp.wordle

import junit.framework.TestCase.assertEquals
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.*
import org.junit.Before
import org.junit.Test


class WordleGameStateTest {
    var helloWordle = WordleGameState(false, listOf("hello"), listOf("hello"))

    @Test
    fun getTilesWorksWithEmptyTileGrid() {
        var tiles = helloWordle.getTiles()
        for (i in 0..5 * 8 - 1) {
            assertEquals('\u0000',tiles.get(i).letter, )
            assertEquals( WordleGameState.TileState.EMPTY, tiles.get(i).state)
        }
    }
    @Test
    fun submitWordAndGetTilesWorksWithHello() {
        var wordle = helloWordle.submitWord("hello")
        var tiles = wordle.getTiles()
       assertEquals('h',tiles.get(0).letter)
        assertEquals('e',tiles.get(1).letter)
        assertEquals('l',tiles.get(2).letter)
        assertEquals('l',tiles.get(3).letter)
        assertEquals('o',tiles.get(4).letter)
        assertEquals('\u0000',tiles.get(5).letter)
        for(i in 0..4){
            assertEquals(WordleGameState.TileState.CORRECT,tiles.get(i).state)
        }
    }

    @Test
    fun submitWordAndGetGridWorksWithHello() {
        var wordle = helloWordle.submitWord("hello")
        var grid = wordle.getGrid()
        assertEquals('h',grid[0][0].letter)
        assertEquals('e',grid[0][1].letter)
        assertEquals('l',grid[0][2].letter)
        assertEquals('l',grid[0][3].letter)
        assertEquals('o',grid[0][4].letter)
        assertEquals('\u0000',grid[1][0].letter)
        for(i in 0..4){
            assertEquals(WordleGameState.TileState.CORRECT,grid[0][1].state)
        }
    }



}