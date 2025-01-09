package com.volodya.volodyaquiz.models

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.volodya.volodyaquiz.R
import java.util.Locale
import kotlin.math.max

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    companion object {
        private val questionBank = arrayOf(
            Question(R.string.question_coke, true),
            Question(R.string.question_android, true),
            Question(R.string.question_onion, false),
            Question(R.string.question_windows, false),
            Question(R.string.question_python, false)
        )
        val bankSize
            get() = questionBank.size

        const val MAX_HINTS = 3
    }

    var currentIndex = 0
        set(value) {
            currentQuestion = questionBank[value]
            field = value
        }

    private var currentQuestion = questionBank[currentIndex]
    var answeredList = BooleanArray(questionBank.size) { false }
    var cheatedList = BooleanArray(questionBank.size) { false }
    var correctAnswers = 0

    val isAllAnswered: Boolean
        get() = answeredList.all { it }

    val isQuestionAnswered: Boolean
        get() = answeredList[currentIndex]

    @get:StringRes
    val questionResId: Int
        get() = currentQuestion.textResId

    val result: String
        get() {
            return String.format(
                Locale.US,
                "%.1f",
                (correctAnswers.toFloat() / questionBank.size.toFloat()) * 100
            ) + '%'
        }

    val currentAnswer: Boolean
        get() = currentQuestion.answer

    var isCheater: Boolean
        get() = cheatedList[currentIndex]
        set(value) {
            cheatedList[currentIndex] = value
        }

    val availableHints: Int
        get() = max(MAX_HINTS - cheatedList.count { it }, 0)

    fun isAnswerCorrect(answer: Boolean): Boolean {
        return answer == currentQuestion.answer
    }

    fun prevQuestion() {
        if (currentIndex == 0) currentIndex = questionBank.size - 1
        else --currentIndex
    }

    fun nextQuestion() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun addAnswer(answer: Boolean) {
        if (isAnswerCorrect(answer)) ++correctAnswers
        answeredList[currentIndex] = true
    }

    fun reset() {
        answeredList.fill(false)
        correctAnswers = 0
    }
}