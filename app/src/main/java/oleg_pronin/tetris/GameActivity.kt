package oleg_pronin.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import oleg_pronin.tetris.databinding.ActivityGameBinding
import oleg_pronin.tetris.databinding.ActivityMainBinding
import oleg_pronin.tetris.storage.AppPreferences

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        preferences = AppPreferences(this)

        setContentView(binding.root)

        updateCurrentScore()
        updateHighScore()
    }

    private fun updateCurrentScore() {
        binding.tvHighScore.text = "${preferences.getHighScore()}"
    }

    private fun updateHighScore() {
        binding.tvCurrentScore.text = "0"
    }
}