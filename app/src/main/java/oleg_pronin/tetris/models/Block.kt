package oleg_pronin.tetris.models

import android.graphics.Color
import android.graphics.Point
import oleg_pronin.tetris.constants.FieldConstants
import java.util.*

class Block(private val shapeIndex: Int, private val color: BlockColor) {
    private var frameNumber = 0
    private var position: Point

    init {
        position = Point(FieldConstants.COLUMN_COUNT.value / 2, 0)
    }

    fun setState(frame: Int, position: Point) {
        frameNumber = frame
        this.position = position
    }

    fun getShape(frameNumber: Int): Array<ByteArray> {
        return Shape.values()[shapeIndex].getFrame(frameNumber).as2dByteArray()
    }

    fun getColor(): Int {
        return color.rgbValue
    }

    val staticValue: Byte
        get() = color.byteValue.toByte()

    val frameCount: Int
        get() = Shape.values()[shapeIndex].frameCount

    enum class BlockColor(val rgbValue: Int, byteValue: Byte) {
        PINK(Color.rgb(255, 105, 180), 2.toByte()),
        GREEN(Color.rgb(0, 128, 0), 3.toByte()),
        ORANGE(Color.rgb(255, 140, 0), 4.toByte()),
        YELLOW(Color.rgb(255, 255, 0), 5.toByte()),
        CYAN(Color.rgb(0, 255, 255), 6.toByte());

        val byteValue: Int = byteValue.toInt()
    }

    companion object {
        fun createBlock(): Block {
            val random: Random = Random()
            val shareIndex: Int = random.nextInt(Shape.values().size)
            val blockColor: BlockColor = BlockColor.values()[random.nextInt(BlockColor.values().size)]
            val block: Block = Block(shareIndex, blockColor)

            block.position.x = block.position.x - Shape.values()[shareIndex].startPosition

            return block
        }

        fun getColor(value: Byte): Int {
            for (color in BlockColor.values()) {
                if (value.toInt() == color.byteValue) {
                    return color.rgbValue
                }
            }

            return -1
        }
    }
}