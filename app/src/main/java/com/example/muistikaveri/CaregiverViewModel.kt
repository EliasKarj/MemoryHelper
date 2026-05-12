package com.example.muistikaveri

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
class CaregiverViewModel : ViewModel() {

    // MainActivity tarvitsee tämän:
    var patientData by mutableStateOf(Patient())
        private set

    var lastKnownLocation by mutableStateOf("Sijaintia ladataan...")
        private set

    fun updateLocation(newLocation: String) {
        lastKnownLocation = newLocation
    }

    // Päivittää potilaan yleisen tilan, ei sisältöä
    fun updateStatus(newStatus: String) {
        patientData = patientData.copy(status = newStatus)
    }
}