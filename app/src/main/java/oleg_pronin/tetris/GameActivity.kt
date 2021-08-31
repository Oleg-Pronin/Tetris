package oleg_pronin.tetris

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import oleg_pronin.tetris.databinding.ActivityGameBinding
import oleg_pronin.tetris.models.AppModel
import oleg_pronin.tetris.storage.AppPreferences
import oleg_pronin.tetris.views.TetrisView

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    var appPreferences: AppPreferences? = null
    private val appModel: AppModel = AppModel()
    private lateinit var tetrisView: TetrisView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        tetrisView = binding.viewTetris
        setContentView(binding.root)

        appPreferences = AppPreferences(this)
        appModel.setPreferences(appPreferences)

        tetrisView.setActivity(this)
        tetrisView.setModel(appModel)
        tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        binding.btnReset.setOnClickListener(this::onBtnRestartClick)

        updateHighScore()
        updateCurrentScore("${appPreferences?.getHighScore()}")
    }

    private fun onBtnRestartClick(view: View) {
        appModel.restartGame()
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent): Boolean {
        if (appModel.isGameOver() || appModel.isGameAwaitingStart()) {
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        } else if (appModel.isGameActive()) {
            when (resolveTouchDirection(view, event)) {
                0 -> moveTetromino(AppModel.Motions.LEFT)
                1 -> moveTetromino(AppModel.Motions.ROTATE)
                2 -> moveTetromino(AppModel.Motions.DOWN)
                3 -> moveTetromino(AppModel.Motions.RIGHT)
            }
        }

        return true
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height

        val direction: Int = if (y > x) {
            if (x > 1 - y) 2 else 0
        } else {
            if (x > 1 - y) 3 else 1
        }

        return direction
    }

    private fun moveTetromino(motion: AppModel.Motions) {
        if (appModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }

    fun updateCurrentScore(value: String) {
        binding.tvHighScore.text = value
    }

    fun updateHighScore(value: String = "0") {
        binding.tvCurrentScore.text = value
    }
}