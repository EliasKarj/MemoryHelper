package com.example.muistikaveri

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverHomeScreen(
    patient: Patient,            // Käytetään Models.kt:n Patient-luokkaa
    routines: List<Routine>,     // Käytetään tarkkaa Routine-tyyppiä Any:n sijaan
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Omaisen seuranta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Takaisin")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
        ) {
            // Potilastiedot
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Potilas: ${patient.name}", fontWeight = FontWeight.Bold)
                    Text(text = "Tila: ${patient.status}", color = Color(0xFF4CAF50))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rutiinien yhteenveto
            Text(text = "Päivän rutiinit", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Potilaalla on ${routines.size} tehtävää tälle päivälle.", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Yksityisyys-ilmoitus (tärkeä potilaan chatin suojaksi)
            Surface(
                color = Color(0xFFECEFF1),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Yksityisyys: Keskusteluhistoria on suojattu, eikä se näy tällä näkymällä.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}