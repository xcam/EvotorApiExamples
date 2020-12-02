package ru.xcam.evotor.example.utils

import android.util.Log


object Logger {
    @JvmStatic
    fun log(message: String? = null) {
        Log.e(
            "EvApiExample",
            (message?.plus(" ") ?: "") + "\n\t" + RuntimeException().stackTrace[1].toString()
        )
    }
}