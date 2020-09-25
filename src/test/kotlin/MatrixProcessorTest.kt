import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.truncate

internal class MatrixProcessorTest {

    private fun convert(src: List<List<Any>>): Array<Array<Number>> {
        var res: Array<Array<Number>> = arrayOf()
        for (i in src.indices) {
            var values: Array<Number> = arrayOf()
            for (j in src[0].indices)
                if (src[i][j] is Int) values += src[i][j].toString().toInt() else values += src[i][j].toString().toDouble()
            res += values
        }
        return res
    }

    private fun Number.round(dec: Int = 2): Number {
        return when (this) {
            0.0, -0.0 -> 0
            is Int -> this
            else -> truncate(this.toDouble() * 10.0.pow(dec)) / 10.0.pow(dec)
            //(this.toDouble() * 10.0.pow(dec)).roundToInt().toDouble() / 10.0.pow(dec)
            //"%.${dec}f".format(this).toDouble()
        }
    }

    @Test
    fun processCmd() {
        // Hyperskill examples
        var a = Matrix(4, 4)
        var b = Matrix(4, 4)
        a.data = convert(listOf(listOf(1, 1, 1, 1), listOf(2, 2, 2, 2), listOf(3, 3, 3, 3), listOf(4, 4, 4, 4)))
        b.data = convert(listOf(listOf(1, 2, 3, 4), listOf(1, 2, 3, 4), listOf(1, 2, 3, 4), listOf(1, 2, 3, 4)))
        assertArrayEquals(a.transpose(TranspositionType.Main).data, b.data)

        a.data = convert(listOf(listOf(1, 1, 1, -1), listOf(2, 2, 2, -2), listOf(3, 3, 3, -3), listOf(4, 4, 4, -4)))
        b.data = convert(listOf(listOf(-4, -3, -2, -1), listOf(4, 3, 2, 1), listOf(4, 3, 2, 1), listOf(4, 3, 2, 1)))
        assertArrayEquals(a.transpose(TranspositionType.Side).data, b.data)

        a.data = convert(listOf(listOf(1, 2, 3, 4), listOf(5, 6, 7, 8), listOf(9, 10, 11, 12), listOf(13, 14, 15, 16)))
        b.data = convert(listOf(listOf(4, 3, 2, 1), listOf(8, 7, 6, 5), listOf(12, 11, 10, 9), listOf(16, 15, 14, 13)))
        assertArrayEquals(a.transpose(TranspositionType.Vertical).data, b.data)

        b.data = convert(listOf(listOf(13, 14, 15, 16), listOf(9, 10, 11, 12), listOf(5, 6, 7, 8), listOf(1, 2, 3, 4)))
        assertArrayEquals(a.transpose(TranspositionType.Horizontal).data, b.data)

        a.data = convert(listOf(listOf(1, 2, 3, 4), listOf(5, 6, 7, 8), listOf(9, 10, 11, 12), listOf(13, 14, 15, 16)))
        b.data = convert(listOf(listOf(13, 14, 15, 16), listOf(9, 10, 11, 12), listOf(5, 6, 7, 8), listOf(1, 2, 3, 4)))
        assertArrayEquals(a.transpose(TranspositionType.Horizontal).data, b.data)

        a.data = convert(listOf(listOf(1, 2, 2, 7), listOf(3, 3, 4, 5), listOf(5, 0, 0, 1), listOf(0, 1, 0, 8)))
        b.data = convert(listOf(listOf(9, 8, 7, 13), listOf(15, 14, 0, 1), listOf(3, 7, 2, 3), listOf(0, 9, 0, 35)))
        var c = Matrix(4, 4)
        c.data = convert(listOf(listOf(45, 113, 11, 266), listOf(84, 139, 29, 229), listOf(45, 49, 35, 100), listOf(15, 86, 0, 281)))
        assertArrayEquals((a * b)!!.data, c.data)

        a = Matrix(2,3)
        b = Matrix(3,4)
        a.data = convert(listOf(listOf(1, 0, 17), listOf(15, 19, 7)))
        b.data = convert(listOf(listOf(5, 6, 78, 9), listOf(29, 31, 47, 1), listOf(14, 17, 0, 3)))
        c = Matrix(2, 4)
        c.data = convert(listOf(listOf(243, 295, 78, 60), listOf(724, 798, 2063, 175)))
        assertArrayEquals((a * b)!!.data, c.data)

        // My own tests
        val x = Matrix(3, 4)
        val y = Matrix(4, 3)
        x.data = convert(listOf(listOf(1, 1, 1, -1), listOf(2, 2, 2, -2), listOf(3, 3, 3, -3)))
        y.data = convert(listOf(listOf(-3, -2, -1), listOf(3, 2, 1), listOf(3, 2, 1), listOf(3, 2, 1)))
//        println(a.data.joinToString("\n") { it.joinToString(" ") })
//        println(a.transpose(TranspositionType.Side).data.joinToString("\n") { it.joinToString(" ") })
        assertArrayEquals(x.transpose(TranspositionType.Side).data, y.data)

        a = Matrix(3,3)
        a.data = convert(listOf(listOf(1, 7, 7), listOf(6, 6, 4), listOf(4, 2, 1)))
        assertEquals(a.determinant(), -16.0)

        a.data = convert(listOf(listOf(8, 2, 3), listOf(1, 0, 1), listOf(2, 5, 1)))
        assertEquals(a.determinant(), -23.0)

        a.data = convert(listOf(listOf(4, -1, 3), listOf(0, 6, 4), listOf(0, 0, -2)))
        assertEquals(a.determinant(), -48.0)

        a = Matrix(4,4)
        a.data = convert(listOf(listOf(6, 5, 0, 0), listOf(1, 2, 0, 0), listOf(7, 3, -2, -4), listOf(-1, -3, -2, 9)))
        assertEquals(a.determinant(), -182.0)

        a = Matrix(5,5)
        a.data = convert(listOf(listOf(1, 2, 3, 4, 5), listOf(4, 5, 6, 4, 3), listOf(0, 0, 0, 1, 5), listOf(1, 3, 9, 8, 7), listOf(5, 8, 4, 7, 11)))
        assertEquals(a.determinant(), 191.0)

        a = Matrix(2,2)
        a.data = convert(listOf(listOf(2, 1), listOf(4, 2)))
        assertEquals(a.inverse(), null)

        a = Matrix(2,2)
        a.data = convert(listOf(listOf(1, 2), listOf(3, 4)))
        b.data = convert(listOf(listOf(-2.0, 1.0), listOf(1.5, -0.5)))
        assertArrayEquals(a.inverse()!!.data, b.data)

        a = Matrix(3,3)
        a.data = convert(listOf(listOf(2, -1, 0), listOf(0, 1, 2), listOf(1, 1, 0)))
        b.data = convert(listOf(listOf(0.33, 0, 0.33), listOf(-0.33, 0, 0.66), listOf(0.16, 0.5, -0.33)))
        c.data = a.inverse()!!.data
        c.data.indices.forEach { i ->
            c.data[i].indices.forEach { j->
                c.data[i][j] = c.data[i][j].round()
            }
        }
        assertArrayEquals(c.data, b.data)
    }
}