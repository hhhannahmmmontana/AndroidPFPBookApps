package com.volodya.volodyaquiz

import android.app.ActivityOptions
import android.os.Bundle
import android.view.View

import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.volodya.volodyaquiz.models.QuizViewModel

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_INDEX = "index"
        private const val KEY_CORRECT_ANSWERS = "correctAnswers"
        private const val KEY_ANSWERS_LIST = "answersList"
        private const val KEY_CHEATED_LIST = "cheatedList"
    }

    private val quizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }
    private lateinit var questionTextView: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton

    private val cheatActivityRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            quizViewModel.isCheater = quizViewModel.isCheater or CheatActivity.wasAnswerShown(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        restoreData(savedInstanceState)

        initializeViews()
        updateQuestion()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putInt(KEY_CORRECT_ANSWERS, quizViewModel.correctAnswers)
        outState.putBooleanArray(KEY_ANSWERS_LIST, quizViewModel.answeredList)
        outState.putBooleanArray(KEY_CHEATED_LIST, quizViewModel.cheatedList)
    }

    private fun initializeViews() {
        questionTextView = findViewById(R.id.question_text_view)
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        cheatButton = findViewById(R.id.cheat_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)

        questionTextView.setOnClickListener {
            quizViewModel.nextQuestion()
        }
        trueButton.setOnClickListener {
            addAnswer(true)
        }
        falseButton.setOnClickListener {
            addAnswer(false)
        }
        cheatButton.setOnClickListener { view ->
            createCheatActivity(view)
        }
        prevButton.setOnClickListener {
            quizViewModel.prevQuestion()
            updateQuestion()
        }
        nextButton.setOnClickListener {
            quizViewModel.nextQuestion()
            updateQuestion()
        }
    }

    private fun createCheatActivity(view: View) {
        val options = ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
        cheatActivityRegister.launch(CheatActivity.newIntent(
            this,
            quizViewModel.currentAnswer,
            quizViewModel.availableHints,
            quizViewModel.isCheater),
            options)
    }

    private fun restoreData(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        quizViewModel.currentIndex =
            savedInstanceState.getInt(KEY_INDEX, 0)
        quizViewModel.correctAnswers =
            savedInstanceState.getInt(KEY_CORRECT_ANSWERS, 0)
        quizViewModel.answeredList =
            savedInstanceState.getBooleanArray(KEY_ANSWERS_LIST)
                ?: quizViewModel.answeredList
        quizViewModel.cheatedList =
            savedInstanceState.getBooleanArray(KEY_CHEATED_LIST)
                ?: quizViewModel.cheatedList
    }

    private fun addAnswer(answer: Boolean) {
        quizViewModel.addAnswer(answer)
        updateButtonsState()
        showToast(answer, quizViewModel.isCheater)
    }

    private fun updateQuestion() {
        updateButtonsState()
        setText()
    }

    private fun updateButtonsState() {
        val state = !quizViewModel.isQuestionAnswered
        trueButton.isEnabled = state
        falseButton.isEnabled = state
    }

    private fun setText() {
        questionTextView.setText(quizViewModel.questionResId)
    }

    private fun showToast(answer: Boolean, cheated: Boolean) {
        if (quizViewModel.isAllAnswered) showResult()
        else {
            Toast.makeText(
                this,
                if (cheated)
                    R.string.cheating_is_wrong
                else if (quizViewModel.isAnswerCorrect(answer))
                    R.string.correct_toast
                else
                    R.string.incorrect_toast,
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showResult() {
        Toast.makeText(
            this,
            quizViewModel.result,
            Toast.LENGTH_SHORT)
            .show()
    }
}