package oleg_pronin.tetris.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private var nameSetting: String = "APP_PREFERENCES"
    private var nameSettingField: String = "HIGH_SCORE"

    private var data: SharedPreferences = context.getSharedPreferences(nameSetting, Context.MODE_PRIVATE)

    fun saveHighScore(highScore: Int) {
        data.edit().putInt(nameSettingField, highScore).apply()
    }

    fun getHighScore(): Int {
        return data.getInt(nameSettingField, 0)
    }

    fun clearHighScore() {
        data.edit().putInt(nameSettingField, 0).apply()
    }
}