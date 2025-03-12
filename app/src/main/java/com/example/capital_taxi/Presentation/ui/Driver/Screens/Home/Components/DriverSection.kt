package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components


enum class DriverSection(val displayName: String) {
    INBOX("Inbox"),
    INVITE_FRIENDS("Invite Friends"),
    HELP("Help"),
    NOTIFICATIONS("Notifications"),
    INCOME("Income"),
    WALLET("Wallet");

    companion object {
        fun getAllSections(): List<DriverSection> {
            return values().toList()
        }
    }
}
