package com.volodya.volodyaquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import kotlin.math.max

class CheatActivity : AppCompatActivity() {
    companion object {
        fun newIntent(
            context: Context,
            answer: Boolean,
            hints: Int,
            isAnswerAlreadyShown: Boolean): Intent
        {
            return Intent(context, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER, answer)
                putExtra(EXTRA_HINTS, hints)
                putExtra(EXTRA_IS_ANSWER_ALREADY_SHOWN, isAnswerAlreadyShown)
            }
        }
        fun wasAnswerShown(result: ActivityResult): Boolean {
            return (result.resultCode == Activity.RESULT_OK) and
                    (result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false)
        }
        private const val EXTRA_ANSWER_SHOWN = "answerShown"
        private const val KEY_ANSWER_SHOWN = "answerShown"
        private const val EXTRA_ANSWER = "answer"
        private const val EXTRA_HINTS = "hints"
        private const val EXTRA_IS_ANSWER_ALREADY_SHOWN = "answerAlreadyShown"
        private const val TAG = "CheatActivity"
    }

    private lateinit var mainFrameLayout: FrameLayout
    private lateinit var cheatTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var hintsTextView: TextView
    private var isAnswerShown = false
    private var answer = false
    private var hints = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        restoreData(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        getExtras()

        initializeViews()
        if (isAnswerShown) setAnswerShown()
        setButtonState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ANSWER_SHOWN, isAnswerShown)
    }

    private fun getExtras() {
        answer = intent.getBooleanExtra(EXTRA_ANSWER, false)
        hints = intent.getIntExtra(EXTRA_HINTS, 0)
        isAnswerShown = intent.getBooleanExtra(EXTRA_IS_ANSWER_ALREADY_SHOWN, false)
    }

    private fun restoreData(savedInstanceState: Bundle?) {
        isAnswerShown = savedInstanceState?.getBoolean(KEY_ANSWER_SHOWN, false) ?: false
    }

    private fun setAnswerShown() {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, true)
        }
        setResult(Activity.RESULT_OK, data)
        makeCheatText()
        updateHintsText()
    }

    private fun initializeViews() {
        mainFrameLayout = findViewById(R.id.activity_cheat_frame_layout)
        cheatTextView = findViewById(R.id.cheat_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        hintsTextView = findViewById(R.id.hints_text_view)

        showAnswerButton.setOnClickListener {
            if (!isAnswerShown) {
                isAnswerShown = true
                hints = max(0, hints - 1)
                setAnswerShown()
                setButtonState()
            }
        }
        makeApiVersionText()
        updateHintsText()
    }

    // this is the book's exercise, i'd prefer to do this thing in the layout
    private fun makeApiVersionText() {
        val layoutParams =
            FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        val apiVersionTextView = TextView(this)
        apiVersionTextView.layoutParams = layoutParams
        apiVersionTextView.text = getString(R.string.api_version, Build.VERSION.SDK_INT)
        apiVersionTextView.setPadding(100)

        mainFrameLayout.addView(apiVersionTextView)
    }

    private fun makeCheatText() {
        cheatTextView.setText(
            if (answer) R.string.true_answer else R.string.false_answer
        )
    }

    private fun setButtonState() {
        showAnswerButton.isEnabled = (hints > 0) and (!isAnswerShown)
    }

    private fun updateHintsText() {
        hintsTextView.text = getString(R.string.hints_amount, hints)
    }
}