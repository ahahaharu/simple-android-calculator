package com.example.simplecalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private var firstOperand: Double = 0.0
    private var secondOperand: Double = 0.0
    private var currentOperator: String = ""
    private var isNewInput: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainContainer = findViewById<View>(R.id.mainContainer)
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvResult = findViewById(R.id.tvResult)

        setupButtons()
    }

    private fun setupButtons() {
        val numberIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        numberIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener { view ->
                val button = view as Button
                onNumberClick(button.text.toString())
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener { onDotClick() }

        val operatorIds = listOf(
            R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide
        )
        operatorIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener { view ->
                val button = view as Button
                onOperatorClick(button.text.toString())
            }
        }

        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsClick() }
        findViewById<Button>(R.id.btnAC).setOnClickListener { onClearClick() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercentClick() }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { onPlusMinusClick() }
    }

    private fun onNumberClick(number: String) {
        if (isNewInput) {
            tvResult.text = number
            isNewInput = false
        } else {
            if (tvResult.text.length < 15) {
                tvResult.text = "${tvResult.text}$number"
            }
        }
    }

    private fun onDotClick() {
        if (isNewInput) {
            tvResult.text = "0."
            isNewInput = false
        } else if (!tvResult.text.contains(".")) {
            tvResult.text = "${tvResult.text}."
        }
    }

    private fun onOperatorClick(operator: String) {
        firstOperand = tvResult.text.toString().toDoubleOrNull() ?: 0.0
        currentOperator = operator
        isNewInput = true
    }

    private fun onEqualsClick() {
        secondOperand = tvResult.text.toString().toDoubleOrNull() ?: 0.0
        var result = 0.0

        when (currentOperator) {
            "+" -> result = firstOperand + secondOperand
            "-" -> result = firstOperand - secondOperand
            "X" -> result = firstOperand * secondOperand
            "/" -> {
                if (secondOperand != 0.0) {
                    result = firstOperand / secondOperand
                } else {
                    result = Double.NaN
                }
            }
        }


        if (result % 1 == 0.0) {
            tvResult.text = result.toInt().toString()
        } else {
            tvResult.text = result.toString()
        }

        isNewInput = true
    }

    private fun onClearClick() {
        tvResult.text = "0"
        firstOperand = 0.0
        secondOperand = 0.0
        currentOperator = ""
        isNewInput = true
    }

    private fun onPercentClick() {
        val value = tvResult.text.toString().toDoubleOrNull() ?: 0.0
        tvResult.text = (value / 100).toString()
        isNewInput = true
    }

    private fun onPlusMinusClick() {
        val value = tvResult.text.toString().toDoubleOrNull() ?: 0.0
        if (value != 0.0) {
            if (value % 1 == 0.0) {
                tvResult.text = (value * -1).toInt().toString()
            } else {
                tvResult.text = (value * -1).toString()
            }
        }
    }
}