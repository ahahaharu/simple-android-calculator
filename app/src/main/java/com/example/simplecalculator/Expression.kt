package com.example.simplecalculator

object Expression {

    fun eval(expression: String): Double {
        val cleaned = expression.replace(",", ".").replace(" ", "")
        val processed = preprocess(cleaned)

        var prepared = processed
            .replace("×", "*")
            .replace("X", "*")
            .replace("x", "*")

        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < prepared.length) prepared[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < prepared.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = prepared.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                return x
            }
        }.parse()
    }

    private fun preprocess(input: String): String {
        var str = input.replace("X", "*")


        val regex = Regex("(\\d+(?:\\.\\d+)?)([+\\-*/])(\\d+(?:\\.\\d+)?)%")

        str = regex.replace(str) { match ->
            val num1 = match.groupValues[1]
            val op = match.groupValues[2]
            val percent = match.groupValues[3]

            when (op) {
                "+" -> "$num1+($num1*$percent/100)"
                "-" -> "$num1-($num1*$percent/100)"
                "*" -> "$num1*($percent/100)"
                "/" -> "$num1/($percent/100)"
                else -> match.value
            }
        }


        return str.replace("%", "/100")
    }
}