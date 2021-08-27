package oleg_pronin.tetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import oleg_pronin.tetris.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    }

    private fun onBtnExitClick(view: View) {
        exitProcess(0)
    }
}