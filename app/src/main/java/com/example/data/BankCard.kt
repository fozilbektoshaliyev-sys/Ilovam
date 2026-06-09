package com.example.data

data class BankCard(
    val id: String,
    val cardType: String, // "Uzcard", "Humo", "Visa"
    val cardNumber: String, // e.g. "8600 1234 5678 9012"
    val expiry: String, // "12/29"
    val holderName: String,
    val balance: Double // simulated balance in UZS or USD
)
