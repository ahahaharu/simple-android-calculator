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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContainer)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
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
            findViewById<Button>(id).setOnClickListener { onButtonClick(it as Button) }
        }
    }

    private fun onButtonClick(btn: Button) {
        val input = btn.text.toString()
        val currentText = tvResult.text.toString()

        when (input) {
            "AC" -> {
                tvResult.text = "0"
                isResultCalculated = false
            }
            "=" -> {
                try {
                    val result = Expression.eval(currentText)
                    tvResult.text = formatResult(result)
                    isResultCalculated = true
                } catch (e: Exception) {
                    tvResult.text = "Error"
                    isResultCalculated = true
                }
            }
            "+/-" -> {
                tvResult.text = toggleSign(currentText)
            }
            "%" -> {
                if (currentText != "Error" && currentText.last().isDigit()) {
                    tvResult.append("%")
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
            return
        }

        if (isResultCalculated && !isOperator(input)) {
            tvResult.text = input
            isResultCalculated = false
            return
        }

        if (isResultCalculated && isOperator(input)) {
            isResultCalculated = false
        }

        if (currentText == "0" && !isOperator(input) && input != ".") {
            tvResult.text = input
            return
        }

        if (isOperator(input) && currentText.isNotEmpty()) {
            val lastChar = currentText.last().toString()
            if (isOperator(lastChar)) {
                tvResult.text = currentText.dropLast(1) + input
                return
            }
        }

        tvResult.append(input)
    }

    private fun toggleSign(displayValue: String): String {
        if (displayValue == "Error" || displayValue == "0") return displayValue

        val lastOpIndex = findLastOperatorIndex(displayValue)

        val baseString = if (lastOpIndex != -1) displayValue.substring(0, lastOpIndex + 1) else ""
        val currentNumber = if (lastOpIndex != -1) displayValue.substring(lastOpIndex + 1) else displayValue

        if (currentNumber.isEmpty()) return displayValue

        return if (currentNumber.startsWith("(-") && currentNumber.endsWith(")")) {
            val innerValue = currentNumber.substring(2, currentNumber.length - 1)
            baseString + innerValue
        } else {
            "$baseString(-$currentNumber)"
        }
    }

    private fun findLastOperatorIndex(str: String): Int {
        val operators = listOf("+", "-", "X", "/", "*")
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
        return str == "+" || str == "-" || str == "X" || str == "/" || str == "*"
    }

    private fun formatResult(value: Double): String {
        val df = DecimalFormat("#.##########")
        return df.format(value).replace(",", ".")
    }
}