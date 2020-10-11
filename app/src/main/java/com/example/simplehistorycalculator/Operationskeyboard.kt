package com.example.simplehistorycalculator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.operations_keyboard.view.*
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.RoundingMode

enum class KeyboardOperationsType {
    Add,
    Subtract,
    Divide,
    Multiply;

    fun symbolForType() : String = when (this) {
        Add -> "+"
        Subtract -> "−"
        Multiply -> "×"
        Divide -> "÷"
    }
    fun arithmeticSymbolForType() : String = when (this) {
        Add -> "+"
        Subtract -> "-"
        Multiply -> "*"
        Divide -> "/"
    }
}

class Operationskeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var textInput: TextView
    private var butnOne: Button
    private var butnTwo: Button
    private var butnthree: Button
    private var butnFour: Button
    private var butnFive: Button
    private var butnSix: Button
    private var butnSeven: Button
    private var butnEight: Button
    private var butnNine: Button
    private var butnZero: Button
    private var butnDecimal: Button
    private var butnBackSpace: Button
    private var butnAdd: Button
    private var butnSubstract: Button
    private var butnDivide: Button
    private var butnMultiply: Button
    private var butnEquals: Button
    private var inputConnection: InputConnection? = null

    init {
        inflate(context, R.layout.operations_keyboard, this)
        textInput = txtInput
        butnOne = btnOne
        butnTwo = btnTwo
        butnthree = btnThree
        butnFour = btnFour
        butnFive = btnFive
        butnSix = btnSix
        butnSeven = btnSeven
        butnEight = btnEight
        butnNine = btnNine
        butnZero = btnZero
        butnDecimal = btnDecimal
        butnBackSpace = btnBackSpace
        butnEquals = btnEqual

        butnAdd = btnAdd
        butnSubstract = btnSubtract
        butnMultiply = btnMultiply
        butnDivide = btnDivide

        butnOne.setOnClickListener {
            onTapNumberButton(it)
        }
        butnTwo.setOnClickListener {
            onTapNumberButton(it)
        }
        butnthree.setOnClickListener {
            onTapNumberButton(it)
        }
        butnFour.setOnClickListener {
            onTapNumberButton(it)
        }
        butnFive.setOnClickListener {
            onTapNumberButton(it)
        }
        butnSix.setOnClickListener {
            onTapNumberButton(it)
        }
        butnSeven.setOnClickListener {
            onTapNumberButton(it)
        }
        butnEight.setOnClickListener {
            onTapNumberButton(it)
        }
        butnNine.setOnClickListener {
            onTapNumberButton(it)
        }
        butnZero.setOnClickListener {
            onTapNumberButton(it)
        }

        butnBackSpace.setOnClickListener {
            onTapBackSpaceButton(it)
        }

        butnDecimal.setOnClickListener {
            onTapDecimalButton(it)
        }

        butnAdd.setOnClickListener {
            onTapOperationButton(it)
        }
        butnMultiply.setOnClickListener {
            onTapOperationButton(it)
        }
        butnDivide.setOnClickListener {
            onTapOperationButton(it)
        }
        butnSubstract.setOnClickListener {
            onTapOperationButton(it)
        }
        butnEquals.setOnClickListener {
            onTapEqualsButton(it)
        }


    }

    fun setUpInputConnection(ic: InputConnection?) {
        inputConnection = ic
    }
    private val validationRegex: String
        get() = "((\\d{1,8})(\\.\\d{1,2})?)([\\+\\-\\/|\\*](\\d{1,8})(\\.\\d{1,2})?)*[\\.\\+\\-\\/\\*]?"
    private var finalExpressionList = mutableListOf<String>()
    // The  display  list string, due to difference in symbols we need to  show it different
    // ie in screen we need to show * as x  and / as ÷
    private var displayExpressionList = mutableListOf<String>()

    private val displaySymbolForDecimal: String
        get() = "."

    private val symbolForMathDecimal = "."
    private val zeroSymbol = "0"
    private val space = "  "
    private val operationSymbols = listOf(
        KeyboardOperationsType.Divide.symbolForType(),
        KeyboardOperationsType.Multiply.symbolForType(),
        KeyboardOperationsType.Subtract.symbolForType(),
        KeyboardOperationsType.Add.symbolForType()
    )
    private val arithmeticSymbols = listOf(
        KeyboardOperationsType.Divide.arithmeticSymbolForType(),
        KeyboardOperationsType.Multiply.arithmeticSymbolForType(),
        KeyboardOperationsType.Subtract.arithmeticSymbolForType(),
        KeyboardOperationsType.Add.arithmeticSymbolForType()
    )

    private fun isValidRegEx(testStr: String, regex: String): Boolean {
        val pattern = regex.toRegex()
        return pattern.matches(testStr)
    }

    // Get the last pressed character
    private fun lastPressed(): String? {
        // If nothing was pressed before then return nil
        val lastPressed = displayExpressionList.lastOrNull()
        if (lastPressed == null || finalExpressionList.lastOrNull() == null) {
            return null
        }
        // Here we are bothered on the display string only  as that is the one user presses
        return lastPressed.trim().replace("\\s+", "")
    }

    /**
     * Handle number tapped
     */
    private fun onTapNumberButton(view: View) {
        val number = (view as Button).text
        val testExpressionCollection = finalExpressionList.toMutableList()
        testExpressionCollection.add(number.toString())

        if (isValidRegEx(testExpressionCollection.joinToString(""), validationRegex)) {
            finalExpressionList = testExpressionCollection
            displayExpressionList.add(number.toString())
        } else {
            // Ignore button tap
            return
        }
        evaluateExpressionResultWith(finalExpressionList)
    }

    /**
     * Handle decimal tapped
     */
    private fun onTapDecimalButton(view: View) {

        val lastPressed = lastPressed()
        if (lastPressed != null) {
            var tempExpression = finalExpressionList.toMutableList()
            if (lastPressed != displaySymbolForDecimal) {
                if (operationSymbols.contains(lastPressed)) {
                    // this means there is an operation symbol and we are entering decimal next
                    // do add zero prefix
                    tempExpression.add(zeroSymbol)
                    tempExpression.add(symbolForMathDecimal)
                    if (isValidRegEx(tempExpression.joinToString(""), validationRegex)) {
                        finalExpressionList = tempExpression
                        displayExpressionList.add(zeroSymbol)
                        displayExpressionList.add(displaySymbolForDecimal)
                    } else {
                        return
                    }
                } else {
                    tempExpression.add(symbolForMathDecimal)
                    if (isValidRegEx(tempExpression.joinToString(""), validationRegex)) {
                        finalExpressionList = tempExpression
                        displayExpressionList.add(displaySymbolForDecimal)
                    } else {
                        return
                    }
                }
            } else {
                return
            }
        } else {
            finalExpressionList.add(zeroSymbol)
            finalExpressionList.add(symbolForMathDecimal)
            displayExpressionList.add(zeroSymbol)
            displayExpressionList.add(displaySymbolForDecimal)
        }
        evaluateExpressionResultWith(finalExpressionList)
    }

    /**
     * Handle arithmetic operations
     */
    private fun onTapOperationButton(view: View) {
        // If last pressed was + and  i press + again
        val currentSymbol = (view as Button).text.toString()
        val arithmeticSymbol = arithmeticSymbolFor(currentSymbol)
        val lastPressed = lastPressed()
        if (lastPressed != null) {
            if (lastPressed != currentSymbol) {
                // Check if last pressed was an operation
                if (operationSymbols.contains(lastPressed)) {
                    // Replace that operation with current operation
                    displayExpressionList.removeLast()
                    finalExpressionList.removeLast()
                } else if (lastPressed == displaySymbolForDecimal) {
                    // Dont allow operation immediately after a decimal
                    return
                }
            } else {
                return
            }
        } else {
            // Symbols cannot be at the start of an expression
            return
        }
        // Add a space before and after symbol for better visibility
        displayExpressionList.add(space + currentSymbol + space)
        finalExpressionList.add(arithmeticSymbol)
        evaluateExpressionResultWith(finalExpressionList)
    }

    /**
     * Handle Equals to button tap
     */
    private fun onTapEqualsButton(view: View) {
        evaluateExpressionResultWith(finalExpressionList, isExplicitEval = true)
    }

    /**
     * Backspace handling
     */
    private fun onTapBackSpaceButton(view: View) {

        val lastPressed = lastPressed()
        if (lastPressed != null) {
            displayExpressionList.removeLast()
            finalExpressionList.removeLast()
            evaluateExpressionResultWith(finalExpressionList)
        }
    }
    private fun evaluateExpressionResultWith(currentExpression: List<String>, isExplicitEval: Boolean = false) {
        var expression = currentExpression.toMutableList()
        val last: String? = expression.lastOrNull()

        if (last == null) {
            clearFields()
            textInput.text = displayExpressionList.joinToString("")
            return
        }

        var expressionToEvaluate = ""
        // Drop the last decimal to make the expression valid
        if (last == symbolForMathDecimal) {
            expression.removeLast()
            expressionToEvaluate = expression.joinToString("")
        } else if (arithmeticSymbols.contains(last as String)) {
            // We discard operation if it is the last enter ie 10+ will be taken as 10
            expression.removeLast()
            expressionToEvaluate = expression.joinToString("")
        } else {
            expressionToEvaluate = expression.joinToString("")
        }
        // Build an Expression from text
        val finalexpression = ExpressionBuilder(expressionToEvaluate).build()

        try {
            // Calculate the result and display
            val result = finalexpression.evaluate()
            val decimal = BigDecimal(result).setScale(2, RoundingMode.HALF_EVEN)
            val resultText = decimal.toString()
            clearFields()
            inputConnection?.commitText(resultText,resultText.length)
            // ie user pressed equal explicitly
            if (isExplicitEval) {
                resetFieldsWith(resultText)
            }
            txtInput.text = displayExpressionList.joinToString("") + " = " + decimal.toString()
        } catch (ex: ArithmeticException) {
            // Display an error message
            val error = "Error"
            clearFields()
            inputConnection?.commitText("Error",error.length)
            txtInput.text = displayExpressionList.joinToString("") + " = " + error
        }

    }

    // If user taps on equals button, we need to start the next operations from the
    // cumulative result
    private fun resetFieldsWith(result: String) {
        // If this is filled then clear it
        if (!finalExpressionList.isEmpty()) {
            finalExpressionList.clear()
        }
        if (!displayExpressionList.isEmpty()) {
            displayExpressionList.clear()
        }

        for (char in result) {
            val character = char.toString()
            // If the character is . or , we need to replace it with . for math
            if (character == displaySymbolForDecimal) {
                finalExpressionList.add(symbolForMathDecimal)
            } else {
                finalExpressionList.add(character)
            }
            displayExpressionList.add(character)
        }
    }
    private fun arithmeticSymbolFor(symbol: String): String = when (symbol) {
        "+" -> "+"
        "-" -> "-"
        "×" -> "*"
        "÷" -> "/"
        else -> ""
    }

    private fun clearFields() {
        val currentText =
            inputConnection?.getExtractedText(ExtractedTextRequest(), 0)?.text
        currentText?.let {
            val beforCursorText =
                inputConnection?.getTextBeforeCursor(it.length, 0)
            val afterCursorText =
                inputConnection?.getTextAfterCursor(it.length, 0)
            if (beforCursorText != null && afterCursorText != null) {
                inputConnection?.deleteSurroundingText(beforCursorText.length, afterCursorText.length)
            }
        }
        inputConnection?.commitText("",1)
    }
}
fun <E> MutableList<E>.removeLast() {
    val element = this.lastOrNull()
    if (element != null) {
        this.removeAt(this.size -1)
    }
}