package oleg_pronin.tetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import oleg_pronin.tetris.databinding.ActivityMainBinding
import oleg_pronin.tetris.storage.AppPreferences
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        preferences = AppPreferences(this)

        setContentView(binding.root)
        setTextHighScore()

        supportActionBar?.hide()

        binding.btnNewGame.setOnClickListener(this::onBtnNewGameClick)
        binding.btnResetScore.setOnClickListener(this::onBtnResetScoreClick)
        binding.btnExit.setOnClickListener(this::onBtnExitClick)

    }

    private fun onBtnNewGameClick(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun onBtnResetScoreClick(view: View) {
        preferences.clearHighScore()
        Snackbar.make(view, "Score successfully reset", Snackbar.LENGTH_SHORT).show()

        setTextHighScore()
    }

    private fun onBtnExitClick(view: View) {
        exitProcess(0)
    }

    private fun setTextHighScore() {
        binding.tvHighScore.text = (getString(R.string.high_score_default) + preferences.getHighScore())
    }
}