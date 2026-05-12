package com.example.muistikaveri

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class RoutineViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Tämä lista pitää kirjaa rutiineista ja päivittää käyttöliittymän automaattisesti
    val routines = mutableStateListOf<Routine>()

    init {
        listenToRoutines()
    }
    fun toggleRoutineStatus(routine: Routine) {
        val uusiTila = !routine.isCompleted

        db.collection("Patients").document("testi_potilas_1")
            .collection("Routines").document(routine.id)
            .update("isCompleted", uusiTila)
            .addOnSuccessListener {
                println("Rutiinin tila päivitetty: $uusiTila")
            }
            .addOnFailureListener { e ->
                println("Virhe päivityksessä: ${e.message}")
            }
    }
    private fun listenToRoutines() {
        // Kuunnellaan testi_potilas_1:n alikokoelmaa "Routines"
        db.collection("Patients").document("testi_potilas_1")
            .collection("Routines")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null) {
                    routines.clear()
                    for (doc in snapshot.documents) {
                        val routine = doc.toObject(Routine::class.java)
                        if (routine != null) {
                            // Tallennetaan dokumentin ID rutiiniin, jotta voimme päivittää sitä myöhemmin
                            routines.add(routine.copy(id = doc.id))
                        }
                    }
                }
            }
    }
}