package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var result: TextView
    var displayExpression = ""
    var isNewNumber = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        result = findViewById(R.id.result)
    }

    fun onDigitClick(view: View) {
        val digit = (view as Button).text.toString()

        if (isNewNumber) {
            displayExpression = ""
            isNewNumber = false
        }

        displayExpression += digit
        result.text = displayExpression
    }

    fun onOperatorClick(view: View) {
        val operator = (view as Button).text.toString()

        if (displayExpression.isEmpty()) return
        val lastChar = displayExpression.last()
        if (lastChar in "+-*/%") return

        displayExpression += operator
        result.text = displayExpression
        isNewNumber = false
    }

    fun onClearClick(view: View) {
        displayExpression = ""
        result.text = "0"
        isNewNumber = true
    }

    fun onDeleteClick(view: View) {
        if (displayExpression.isNotEmpty()) {
            displayExpression = displayExpression.dropLast(1)
            result.text = if (displayExpression.isEmpty()) "0" else displayExpression
        }
    }

    fun onEqualClick(view: View) {
        try {
            val value = calculateExpression(displayExpression)
            val formatted = formatResult(value)
            result.text = formatted
            displayExpression = formatted
            isNewNumber = true
        } catch (e: Exception) {
            result.text = "Error"
            displayExpression = ""
            isNewNumber = true
        }
    }

    fun calculateExpression(expression: String): Double {
        val tokens = mutableListOf<String>()
        var number = ""

        for (c in expression) {
            if (c in "+-*/%") {
                tokens.add(number)
                tokens.add(c.toString())
                number = ""
            } else {
                number += c
            }
        }
        tokens.add(number)

        var i = 0
        while (i < tokens.size) {
            if (tokens[i] == "*" || tokens[i] == "/" || tokens[i] == "%") {
                val a = tokens[i - 1].toDouble()
                val b = tokens[i + 1].toDouble()
                val res = when (tokens[i]) {
                    "*" -> a * b
                    "/" -> a / b
                    "%" -> a % b
                    else -> 0.0
                }
                tokens[i - 1] = res.toString()
                tokens.removeAt(i)
                tokens.removeAt(i)
                i--
            } else {
                i++
            }
        }

        var result = tokens[0].toDouble()
        i = 1
        while (i < tokens.size) {
            val op = tokens[i]
            val num = tokens[i + 1].toDouble()
            result = when (op) {
                "+" -> result + num
                "-" -> result - num
                else -> result
            }
            i += 2
        }

        return result
    }

    fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            value.toString()
        }
    }
}
