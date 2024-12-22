package com.mado.ap_simplecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mado.ap_simplecalc.ui.theme.AP_SimpleCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AP_SimpleCalcTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}


@Composable
fun CalculatorScreen() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = input,
                style = TextStyle(fontSize = 32.sp),
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "Result: $result",
                style = TextStyle(fontSize = 24.sp),
                modifier = Modifier.padding(8.dp)
            )
        }


        Column(modifier = Modifier.fillMaxWidth()) {
            val buttons = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("C", "0", "=", "+")
            )

            for (row in buttons) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (button in row) {
                        Button(
                            onClick = {
                                when (button) {
                                    "C" -> {
                                        input = ""
                                        result = "0"
                                    }
                                    "=" -> {
                                        result = try {
                                            evaluateExpression(input).toString()
                                        } catch (e: Exception) {
                                            "Error"
                                        }
                                    }
                                    else -> {
                                        input += button
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(80.dp)
                        ) {
                            Text(text = button, style = TextStyle(fontSize = 24.sp))
                        }
                    }
                }
            }
        }
    }
}


fun evaluateExpression(expression: String): Double {
    return try {
        val result = object : Any() {
            fun parse(): Double = parseExpression().also {
                if (position < expression.length) throw IllegalArgumentException("Unexpected: ${expression[position]}")
            }

            private var position = -1
            private var currentChar: Char = ' '

            init {
                advance()
            }

            private fun advance() {
                currentChar = if (++position < expression.length) expression[position] else '\u0000'
            }

            private fun parseExpression(): Double {
                var result = parseTerm()
                while (true) {
                    when (currentChar) {
                        '+' -> {
                            advance()
                            result += parseTerm()
                        }

                        '-' -> {
                            advance()
                            result -= parseTerm()
                        }

                        else -> return result
                    }
                }
            }

            private fun parseTerm(): Double {
                var result = parseFactor()
                while (true) {
                    when (currentChar) {
                        '*' -> {
                            advance()
                            result *= parseFactor()
                        }

                        '/' -> {
                            advance()
                            result /= parseFactor()
                        }

                        else -> return result
                    }
                }
            }

            private fun parseFactor(): Double {
                when {
                    currentChar in '0'..'9' || currentChar == '.' -> {
                        val start = position
                        while (currentChar in '0'..'9' || currentChar == '.') advance()
                        return expression.substring(start, position).toDouble()
                    }

                    currentChar == '(' -> {
                        advance()
                        val result = parseExpression()
                        if (currentChar == ')') advance()
                        return result
                    }

                    currentChar == '-' -> {
                        advance()
                        return -parseFactor()
                    }

                    else -> throw IllegalArgumentException("Unexpected: $currentChar")
                }
            }
        }
        result.parse()
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid Expression")
    }
}