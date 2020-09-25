import java.util.*
import kotlin.math.pow
import kotlin.math.truncate

const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"
const val ERROR = ANSI_RED + "ERROR" + ANSI_RESET
const val ERROR_INPUT = ANSI_RED + "Incorrect input data" + ANSI_RESET
const val ERROR_DIMENSIONS = ANSI_RED + "Invalid matrix size. Possible values: " + ANSI_RESET
const val ERROR_NOT_SAME_SIZE = ANSI_RED + "Matrices must have the same size" + ANSI_RESET
const val ERROR_NOT_SQUARE = ANSI_RED + "Matrices must have the same size" + ANSI_RESET
const val ERROR_NOT_APPLICABLE = ANSI_RED + "The number of columns of the first matrix is not equal to the number of rows of the second one" + ANSI_RESET
const val ERROR_DET_CANT_BE_ZERO = ANSI_RED + "This matrix doesn't have an inverse." + ANSI_RESET
const val INPUT_SIZE = "Enter size of $$ matrix: "
const val INPUT_DATA = "Enter $$ matrix:"
const val INPUT_SCALAR = "Enter constant:"
const val RESULT_MUL = "The multiplication result is:\n"
const val RESULT_ADD = "The addition result is:\n"
const val MENU = "1. Add matrices\n" +
        "2. Multiply matrix to a constant\n" +
        "3. Multiply matrices\n" +
        "4. Transpose matrix\n" +
        "5. Calculate a determinant\n" +
        "6. Inverse matrix\n" +
        "0. Exit\n" +
        "Your choice: "
const val TRANSPOSE_MENU = "1. Main diagonal\n" +
        "2. Side diagonal\n" +
        "3. Vertical line\n" +
        "4. Horizontal line\n" +
        "Your choice: "
const val RESULT_IS = "The result is:\n"

enum class TranspositionType { Main, Side, Vertical, Horizontal }
val transposeMap = hashMapOf("1" to TranspositionType.Main, "2" to TranspositionType.Side, "3" to TranspositionType.Vertical, "4" to TranspositionType.Horizontal)
val scanner = Scanner(System.`in`)

object MatrixProcessor {
    private val matrixAllowedDimensions = 1..10000

    private fun getMatrix(s: String): Matrix? {
        print(INPUT_SIZE.replace("$$", s))
        val input = readLine()!!.trim()
        val a: Int
        val b: Int
        try {
            a = input.split(" ")[0].toInt()
            b = input.split(" ")[1].toInt()
            if (a !in matrixAllowedDimensions || b !in matrixAllowedDimensions) throw Exception(ERROR_DIMENSIONS + ANSI_RED + matrixAllowedDimensions + ANSI_RESET)
        } catch (e: Exception) {
            e.message?.also { if (it.contains(ERROR_DIMENSIONS)) println(it) else println(ERROR_INPUT) } ?: println(ERROR_INPUT)
            return null
        }
        val res = Matrix(a, b)
        println(INPUT_DATA.replace("$$", s))
        try {
            res.fillData()
        } catch (e: Exception) {
            println(ERROR_INPUT)
            return null
        }
        return res
    }

    private fun getScalar(): Number? = try {
        println(INPUT_SCALAR)
        if (scanner.hasNextInt()) scanner.nextInt() else scanner.nextDouble()
    } catch (e: Exception) {
        println(ERROR_INPUT)
        null
    }

    fun processCmd() {
        loop@ while (true) {
            print(MENU)
            when (readLine()!!.trim()) {
                "1" -> {
                    getMatrix("first")?.also { a ->
                        getMatrix("second")?.also { b ->
                            (a + b)?.also { println(RESULT_ADD + it) }
                        }
                    }
                }
                "2" -> {
                    getMatrix("")?.also { a ->
                        getScalar()?.also { b -> println(RESULT_MUL + a * b) }
                    }
                }
                "3" -> {
                    getMatrix("first")?.also { a ->
                        getMatrix("second")?.also { b ->
                            (a * b)?.also { println(RESULT_MUL + it) }
                        }
                    }
                }
                "4" -> {
                    print(TRANSPOSE_MENU)
                    transposeMap[readLine()!!.trim()]?.also { type ->
                        getMatrix("")?.also { println(RESULT_IS + it.transpose(type)) }
                    } ?: println(ERROR)
                }
                "5" -> {
                    getMatrix("")?.also { a ->
                        a.determinant()?.also { det ->
                            println(RESULT_IS + (if (a.data.flatten().none { it is Double }) det.toInt() else det))
                        }
                    }
                }
                "6" -> getMatrix("")?.also { it.inverse()?.also { inv -> println(RESULT_IS + inv) } }
                "0" -> break@loop
            }
        }
    }

}

class Matrix(private var n: Int, private var m: Int) {
    var data: Array<Array<Number>> = arrayOf()

    private fun Number.round(dec: Int = 2): Number {
        return when (this) {
            0.0, -0.0 -> 0
            is Int -> this
            else -> truncate(this.toDouble() * 10.0.pow(dec)) / 10.0.pow(dec)
            //(this.toDouble() * 10.0.pow(dec)).roundToInt().toDouble() / 10.0.pow(dec)
            //"%.${dec}f".format(this).toDouble()
        }
    }

    override fun toString(): String {
        val maxLen = data.flatten().map { it.round().toString().length }.max()!!.toInt()
        return data.joinToString("\n") { it.joinToString(" ") { j -> j.round().toString().padStart(maxLen) } }
    }

    private fun isSquare() = (n == m)

    fun fillData() {
        for (i in 0 until n) {
            var rows: Array<Number> = arrayOf()
            for (j in 0 until m)
                if (scanner.hasNextInt()) rows += scanner.nextInt() else rows += scanner.nextDouble()
            data += rows
        }
    }

    fun transpose(type: TranspositionType = TranspositionType.Main): Matrix {
        val (nT, mT) = if (type in listOf(TranspositionType.Main, TranspositionType.Side)) arrayOf(m, n) else arrayOf(n, m)
        val tMat = Matrix(nT, mT)
        for (i in 0 until nT) {
            var rows: Array<Number> = arrayOf()
            for (j in 0 until mT)
                rows += when (type) {
                    TranspositionType.Main -> data[j][i]
                    TranspositionType.Side -> data[n - 1 - j][m - 1 - i]
                    TranspositionType.Vertical -> data[i][m - 1 - j]
                    TranspositionType.Horizontal -> data[n - 1 - i][j]
                }
            tMat.data += rows
        }
        return tMat
    }

    fun determinant(xx: Array<Array<Number>> = this.data, ii: Int = -1, jj: Int = -1): Double? {
        if (!isSquare()) {
            println(ERROR_NOT_SQUARE)
            return null
        }

        var det = 0.0
        var x: Array<Array<Number>> = arrayOf()
        for (i in xx.indices) {
            var rows: Array<Number> = arrayOf()
            for (j in xx[0].indices)
                if ((ii == -1) || (i != ii && j != jj)) rows += xx[i][j]
            if (rows.isNotEmpty()) x += rows
        }

        if (x.size == 2) {
            return x[0][0].toDouble() * x[1][1].toDouble() - x[0][1].toDouble() * x[1][0].toDouble()
        }
        val i = x.map { arrayOf(x.indexOf(it), it.count { el -> el.toInt() == 0 }) }.sortedByDescending { row -> row[1] }[0][0]  // or simply i = 0
        for (j in x.indices) {
            det += x[i][j].toDouble() * (-1.0).pow(i + j) * determinant(x, i, j)!!
        }
        return det
    }

    fun inverse(): Matrix? {
        determinant()?.let { det ->
            return if (det != 0.0) {
                minorMatrix().transpose() * (1 / det)
            } else {
                println(ERROR_DET_CANT_BE_ZERO)
                null
            }
        } ?: return null
    }

    private fun minorMatrix(): Matrix {
        var x: Array<Array<Number>> = arrayOf()
        for (i in data.indices) {
            var rows: Array<Number> = arrayOf()
            for (j in data[0].indices)
                rows += if (data.size == 2) (-1.0).pow(i + j) * data[1 - i][1 - j].toDouble()
                else (-1.0).pow(i + j) * determinant(data, i, j)!!
            x += rows
        }
        this.data = x
        return this
    }

    operator fun plus(addend: Matrix): Matrix? {
        if (this.n != addend.n || this.m != addend.m) {
            println(ERROR_NOT_SAME_SIZE)
            return null
        }
        data.indices.forEach { i ->
            data[i].indices.forEach { j ->
                if (data[i][j] is Int && addend.data[i][j] is Int) data[i][j] = data[i][j].toInt() + addend.data[i][j].toInt()
                else data[i][j] = data[i][j].toDouble() + addend.data[i][j].toDouble()
            }
        }
        return this
    }

    operator fun times(scalar: Number): Matrix {
        data.indices.forEach { i ->
            data[i].indices.forEach { j ->
                if (data[i][j] is Int && scalar is Int)
                    data[i][j] = data[i][j].toInt() * scalar.toInt()
                else
                    data[i][j] = data[i][j].toDouble() * scalar.toDouble()
            }
        }
        return this
    }

    operator fun times(ier: Matrix): Matrix? {
        if (this.m != ier.n) {
            println(ERROR_NOT_APPLICABLE)
            return null
        }
        val resMat = Matrix(this.m, ier.n)
        for (i in 0 until this.n) {
            var rows: Array<Number> = arrayOf()
            for (j in 0 until ier.m) {
                var temp = 0.0
                var isDouble = false
                for (k in 0 until this.m) {
                    if (this.data[i][k] is Double || ier.data[k][j] is Double) isDouble = true
                    temp += this.data[i][k].toDouble() * ier.data[k][j].toDouble()
                }
                if (isDouble) rows += temp else rows += temp.toInt()
            }
            resMat.data += rows
        }
        return resMat
    }

}

fun main() {
    MatrixProcessor.processCmd()
    println("Bye bye!")
}