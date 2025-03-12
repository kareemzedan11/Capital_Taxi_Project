package com.example.capital_taxi.Presentation.ui.Passengar.Screens.InviteFriendsPage.Components

import android.content.ClipboardManager
import android.content.Context

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = android.content.ClipData.newPlainText("Referral Code", text)
    clipboard.setPrimaryClip(clip)
}
