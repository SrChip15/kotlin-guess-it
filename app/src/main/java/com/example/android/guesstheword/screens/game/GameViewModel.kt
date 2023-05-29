package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {

    var word = ""
    var score = 0

    private lateinit var wordList: MutableList<String>

    init {
        Log.i("GameViewModel", "GameViewModel created")
        resetList()
        nextWord()
    }


    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    private fun nextWord() {
        if (wordList.isEmpty()) {
            // gameFinished()
        } else {
            word = wordList.removeAt(0)
        }
    }


    fun onSkip() {
        score--
        nextWord()
    }

    fun onCorrect() {
        score++
        nextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "onCleared: GameViewModel destroyed")
    }
}