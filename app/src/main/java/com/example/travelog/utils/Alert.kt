package com.example.travelog.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

/**
 * A simple alert dialog utility that displays a title and message.
 */
class Alert(private val title: String, private val message: String, private val context: Context) {
    /**
     * Shows the alert dialog with an OK button.
     */
    fun show() {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()
    }
}
