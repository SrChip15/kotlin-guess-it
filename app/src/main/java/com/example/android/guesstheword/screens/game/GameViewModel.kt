package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

/**
 * ViewModel containing all the logic needed to run the game
 */
class GameViewModel : ViewModel() {

    /**
     * Enum class representing the three different types of buzz patterns in the game
     * Buzz pattern is the number of milliseconds each interval of buzzing and non-buzzing takes.
     */
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    companion object {
        // These represent the different important times in the game, such as game length.

        // This is when the game is over
        private const val DONE = 0L

        // This is the number of milliseconds in a second
        private const val ONE_SECOND = 1_000L

        // This is the total game time in milliseconds
        private const val COUNTDOWN_TIME = 60_000L

        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L
    }

    private val timer: CountDownTimer
    private val _currentTime = MutableLiveData<Long>()

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    // Event that triggers the phone to buzz using different patterns, determined by BuzzType
    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    // Event which triggers the end of the game
    private val _isGameOver = MutableLiveData<Boolean>()
    val isGameOver: LiveData<Boolean>
        get() = _isGameOver

    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    // The String version of the current time
    val currentTimeString = Transformations.map(_currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    init {
        resetList()
        nextWord()
        _score.value = 0

        // Creates a timer which triggers the end of the game when it finishes
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
                if ((millisUntilFinished / ONE_SECOND) < COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _isGameOver.value = true
                _eventBuzz.value = BuzzType.GAME_OVER
            }
        }

        timer.start()
    }

    // View owner informs the ViewModel that the buzz is complete
    // This is done despite locking the app in Landscape mode, which would otherwise expose the app
    // to configuration changes. This is defensive programming.
    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }

    // View owner informs the ViewModel that the game over event is complete
    fun eventGameOverComplete() {
        _isGameOver.value = false
    }

    /**
     * Resets the list of words and randomizes the order - the word in front of the list
     * would become the word to guess
     */
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

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    /**
     * One of the two buttons in the game that tracks the score
     * This button listener skips the current word and subtracts one point from the score
     */
    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    /**
     * One of the two buttons in the game that tracks the score
     * This button listener adds one point to the score and moves to the next word in the list
     */
    fun onCorrect() {
        _score.value = score.value?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }

    /**
     * The cleanup method in ViewModel base class
     * Overridden to clear timer resources
     */
    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}