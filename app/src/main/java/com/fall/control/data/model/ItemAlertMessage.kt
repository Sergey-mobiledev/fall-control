package com.fall.control.data.model

import kotlin.random.Random

data class ItemAlertMessage(
    val id: Int = Random.nextInt(),
    val message: String,
    val isSelected: Boolean = false
) {

    companion object {
        val itemAlertMessages = listOf(
            ItemAlertMessage(
                message = "Emergency Alert: A sudden fall detected!"
            ),
            ItemAlertMessage(
                message = "Alert: Possible fall detected! Please verify safety"
            ),
            ItemAlertMessage(
                message = "Warning: Device fall detected! Immediate assistance may be needed."
            ),
            ItemAlertMessage(
                message = "I need help please contact me."
            )
        )
    }
}