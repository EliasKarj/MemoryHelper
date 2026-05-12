package com.example.muistikaveri

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

// 1. Kokoelma: Patients
data class Patient(
    val id: String = "",
    val name: String = "",
    val homeLocation: GeoPoint? = null,
    val safetyRadiusMeters: Double = 0.0,
    val currentLocation: GeoPoint? = null,
    val status: String = "safe",
    val caregiverId: String = "",
    val isVoiceOutputEnabled: Boolean = true
)

// 2. Alakokoelma: Routines
data class Routine(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val timeWindowStart: Timestamp? = null,
    val timeWindowEnd: Timestamp? = null,

    // Nämä annotaatiot varmistavat, että Firebase ja Kotlin puhuvat samaa kieltä
    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    val hasBeenReminded: Boolean = false
)

// 3. Kokoelma: Caregivers
data class Caregiver(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val pushToken: String = ""
)