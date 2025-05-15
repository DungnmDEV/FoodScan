package com.base.app.baseapp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View

object Utils {

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun Context.rateApp() {
        val applicationID = this.packageName
        val playStoreUri = Uri.parse("market://details?id=$applicationID")

        val rateIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
        rateIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

        try {
            this.startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val webPlayStoreUri =
                Uri.parse("https://play.google.com/store/apps/details?id=$applicationID")
            val webRateIntent = Intent(Intent.ACTION_VIEW, webPlayStoreUri)
            this.startActivity(webRateIntent)
        }
    }
}