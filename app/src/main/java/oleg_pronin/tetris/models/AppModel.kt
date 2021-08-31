package oleg_pronin.tetris.models

import android.graphics.Point
import oleg_pronin.tetris.constants.CellConstants
import oleg_pronin.tetris.constants.FieldConstants
import oleg_pronin.tetris.helpers.array2dOfByte
import oleg_pronin.tetris.storage.AppPreferences

class AppModel {
    // Определение свойств класса
    var score: Int = 0 // Сохранение текущего счета игрока
    private var preferences: AppPreferences? = null

    var currentBlock: Block? = null // СОдержание текущего блока
    var currentState: String = Statuses.AWAITING_START.name

    // field - игровое поле
    private var field: Array<ByteArray> = array2dOfByte(
        FieldConstants.ROW_COUNT.value,
        FieldConstants.COLUMN_COUNT.value
    )

    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()

            var frameNumber: Int? = currentBlock?.frameNumber
            val coordinate: Point? = Point()

            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            when (action) {
                Motions.LEFT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.minus(1)
                }
                Motions.RIGHT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.plus(1)
                }
                Motions.DOWN.name -> {
                    coordinate?.y = currentBlock?.position?.y?.plus(1)
                }
                Motions.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)

                    if (frameNumber != null) {
                        if (frameNumber >= currentBlock?.frameCount as Int) {
                            frameNumber = 0
                        }
                    }
                }
            }

            if (!moveValid(coordinate as Point, frameNumber)) {
                translateBlock(currentBlock?.position as Point, currentBlock?.frameNumber as Int)

                if (Motions.DOWN.name == action) {
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBLock()

                    if (!blockAdditionPossible()) {
                        currentState = Statuses.OVER.name
                        currentBlock = null
                        resetField(false)
                    }
                }
            } else {
                if (frameNumber != null) {
                    translateBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun resetField(ephemeralCellsOnly: Boolean = true) {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            (0 until FieldConstants.COLUMN_COUNT.value)
                .filter {
                    !ephemeralCellsOnly || field[i][it] == CellConstants.EPHEMERAL.value
                }.forEach {
                    field[i][it] = CellConstants.EMPTY.value
                }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field) {
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)

            if (shape != null) {
                for (i in shape.indices) {
                    for (j in shape[i].indices) {
                        val y = position.y + i
                        val x = position.x + j

                        if (CellConstants.EMPTY.value != shape[i][j]) {
                            field[y][x] = shape[i][j]
                        }
                    }
                }
            }
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?): Boolean {
        val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber as Int)

        return validTranslation(position, shape as Array<ByteArray>)
    }

    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {
        return if (position.y < 0 || position.x < 0) {
            false
        } else if (position.y + shape.size > FieldConstants.ROW_COUNT.value) {
            false
        } else if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value) {
            false
        } else {
            for (i in shape.indices) {
                for (j in shape[i].indices) {
                    val y = position.y + i
                    val x = position.x + j

                    if (CellConstants.EMPTY.value != shape[i][j] && CellConstants.EMPTY.value != field[y][x]) {
                        return false
                    }
                }
            }

            true
        }
    }

    private fun generateNextBLock() {
        currentBlock = Block.createBlock()
    }

    private fun blockAdditionPossible(): Boolean {
        if (!moveValid(currentBlock?.position as Point, currentBlock?.frameNumber)) {
            return false
        }

        return true
    }

    private fun persistCellData() {
        for (i in field.indices) {
            for (j in field[i].indices) {
                var status = getCellStatus(i, j)

                if (status == CellConstants.EPHEMERAL.value) {
                    status = currentBlock?.staticValue!!
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    private fun assessField() {
        for (i in field.indices) {
            var emptyCells = 0

            for (j in field[i].indices) {
                val status = getCellStatus(i, j)
                val isEmpty = CellConstants.EMPTY.value == status

                if (isEmpty) {
                    emptyCells++
                }

                if (emptyCells == 0) {
                    shiftRows(i)
                }
            }
        }
    }

    private fun shiftRows(nToRow: Int) {
        if (nToRow > 0) {
            for (i in nToRow - 1 downTo 0) {
                for (j in field[i].indices) {
                    setCellStatus(i + 1, j, getCellStatus(i, j))
                }
            }
        }

        for (i in field[0].indices) {
            setCellStatus(0, i, CellConstants.EMPTY.value)
        }
    }

    fun setPreferences(preferences: AppPreferences?)
    {
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, column: Int): Byte? {
        return field[row][column]
    }

    private fun setCellStatus(row: Int, column: Int, status: Byte?) {
        if (status != null) {
            field[row][column] = status
        }
    }

    fun startGame() {
        if (!isGameActive()) {
            currentState = Statuses.ACTIVE.name
            generateNextBLock()
        }
    }

    fun restartGame() {
        resetModel()
        startGame()
    }

    fun endGame() {
        score = 0
        currentState = Statuses.OVER.name
    }

    private fun resetModel() {
        resetField(false)
        currentState = Statuses.AWAITING_START.name
        score = 0
    }

    private fun boostScore() {
        score += 10

        if (score > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(score)
        }
    }

    fun isGameOver(): Boolean {
        return currentState == Statuses.OVER.name
    }

    fun isGameActive(): Boolean {
        return currentState == Statuses.ACTIVE.name
    }

    fun isGameAwaitingStart(): Boolean {
        return currentState == Statuses.AWAITING_START.name
    }

    // Игровые статусы в процессе игры
    enum class Statuses {
        AWAITING_START, // Статус до запуска игры
        ACTIVE, // Выполнение игрового процесса
        INACTIVE, // Не выполнение игрового процесса
        OVER // Стутус на момент завершения игры
    }

    // Возможные движения в процессе игры
    enum class Motions {
        LEFT,
        RIGHT,
        DOWN,
        ROTATE
    }
}