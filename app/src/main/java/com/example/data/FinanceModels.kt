package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String, // "Food", "Fuel", "Shopping", "Bills", "Medical", "Travel", "Entertainment", "Subscription", "Others" or "Salary", "Freelance", "Business", "Passive", "Others"
    val note: String,
    val date: Long, // timestamp
    val paymentMethod: String // "UPI", "Card", "Cash", "Bank Transfer"
) : Serializable

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val category: String, // "Laptop", "Bike", "Emergency Fund", "Vacation", "Other"
    val targetDate: Long // target date timestamp
) : Serializable
