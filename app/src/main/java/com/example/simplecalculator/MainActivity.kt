package com.example.simplecalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private var isResultCalculated = false
    private val MAX_INPUT_LENGTH = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainView = findViewById<View>(R.id.mainContainer)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
                insets
            }
        }

        tvResult = findViewById(R.id.tvResult)
        tvResult.text = "0"

        setupButtons()
    }

    private fun setupButtons() {
        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide,
            R.id.btnDot, R.id.btnEquals, R.id.btnAC, R.id.btnPercent, R.id.btnPlusMinus
        )

        buttons.forEach { id ->
            findViewById<Button>(id)?.setOnClickListener { onButtonClick(it as Button) }
        }
    }

    private fun onButtonClick(btn: Button) {
        val input = btn.text.toString()
        val currentText = tvResult.text.toString()

        when (input) {
            "AC" -> {
                tvResult.text = "0"
                isResultCalculated = false
                updateFontSize()
            }
            "=" -> {
                try {
                    val result = Expression.eval(currentText)
                    tvResult.text = formatResult(result)
                    isResultCalculated = true
                    updateFontSize()
                } catch (e: Exception) {
                    tvResult.text = "Error"
                    isResultCalculated = true
                    updateFontSize()
                }
            }
            "+/-" -> {
                tvResult.text = toggleSign(currentText)
                updateFontSize()
            }
            "%" -> {
                if (currentText != "Error" && currentText.isNotEmpty() && currentText.last().isDigit()) {
                    tvResult.append("%")
                    updateFontSize()
                }
            }
            else -> {
                handleInput(input, currentText)
            }
        }
    }

    private fun handleInput(input: String, currentText: String) {
        if (currentText == "Error") {
            tvResult.text = if (isOperator(input)) "0$input" else input
            updateFontSize()
            return
        }

        // Если результат только что выведен, при нажатии цифры начинаем заново
        if (isResultCalculated) {
            if (!isOperator(input)) {
                tvResult.text = if (input == ".") "0." else input
                isResultCalculated = false
                updateFontSize()
                return
            }
            isResultCalculated = false
        }

        // Проверка лимита цифр в текущем вводимом числе (Regex включает × и X)
        val lastNumber = currentText.split(Regex("[+\\-×X/]")).last()
        if (!isOperator(input) && lastNumber.length >= MAX_INPUT_LENGTH && input != ".") {
            return
        }

        // Обработка начального нуля
        if (currentText == "0" && !isOperator(input) && input != ".") {
            tvResult.text = input
            updateFontSize()
            return
        }

        // Замена оператора, если он уже стоит в конце
        if (isOperator(input) && currentText.isNotEmpty()) {
            val lastChar = currentText.last().toString()
            if (isOperator(lastChar)) {
                tvResult.text = currentText.dropLast(1) + input
                updateFontSize()
                return
            }
        }

        tvResult.append(input)
        updateFontSize()
    }

    private fun updateFontSize() {
        val length = tvResult.text.length
        val newSize = when {
            tvResult.text == "0" || tvResult.text == "Error" -> 80f
            length > 12 -> 35f
            length > 8 -> 55f
            else -> 80f
        }
        tvResult.textSize = newSize
    }

    private fun toggleSign(displayValue: String): String {
        if (displayValue == "Error" || displayValue == "0") return displayValue
        val lastOpIndex = findLastOperatorIndex(displayValue)
        val baseString = if (lastOpIndex != -1) displayValue.substring(0, lastOpIndex + 1) else ""
        val currentNumber = if (lastOpIndex != -1) displayValue.substring(lastOpIndex + 1) else displayValue

        if (currentNumber.isEmpty()) return displayValue

        return if (currentNumber.startsWith("(-") && currentNumber.endsWith(")")) {
            currentNumber.substring(2, currentNumber.length - 1).let { baseString + it }
        } else {
            "$baseString(-$currentNumber)"
        }
    }

    private fun findLastOperatorIndex(str: String): Int {
        val operators = listOf("+", "-", "×", "X", "/", "*")
        var depth = 0
        for (i in str.indices.reversed()) {
            val char = str[i]
            if (char == ')') depth++
            if (char == '(') depth--
            if (depth == 0 && operators.contains(char.toString())) {
                if (char == '-' && i == 0) return -1
                return i
            }
        }
        return -1
    }

    private fun isOperator(str: String): Boolean {
        return str == "+" || str == "-" || str == "×" || str == "X" || str == "/" || str == "*"
    }

    private fun formatResult(value: Double): String {
        val df = DecimalFormat("#.##########")
        return df.format(value).replace(",", ".")
    }
}