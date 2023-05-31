package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel() {

    companion object {
        private const val DONE = 0L
        private const val ONE_SECOND = 1_000L
        private const val COUNTDOWN_TIME = 20_000L
    }

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _isGameOver = MutableLiveData<Boolean>()
    val isGameOver: LiveData<Boolean>
        get() = _isGameOver

    private val _currentTime = MutableLiveData<Long>()

    val currentTimeString = Transformations.map(_currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    private val _eventCorrectAnswer = MutableLiveData<Boolean>()
    val eventCorrectAnswer: LiveData<Boolean>
        get() = _eventCorrectAnswer

    private lateinit var wordList: MutableList<String>

    private val timer: CountDownTimer

    private val _countDownActive = MutableLiveData<Boolean>()
    val isCountDownActive: LiveData<Boolean>
        get() = _countDownActive

    init {
        Log.i("GameViewModel", "GameViewModel created")
        resetList()
        nextWord()
        _score.value = 0
        _currentTime.value = Long.MAX_VALUE
        _eventCorrectAnswer.value = false
        _countDownActive.value = false

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
                if (_currentTime.value!! < 10L) _countDownActive.value = true
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _isGameOver.value = true
                _countDownActive.value = false
            }
        }

        timer.start()
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
        _eventCorrectAnswer.value = false
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    fun eventGameOverComplete() {
        _isGameOver.value = false
    }

    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value?.plus(1)
        _eventCorrectAnswer.value = true
        nextWord()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Log.i("GameViewModel", "onCleared: GameViewModel destroyed")
    }
}