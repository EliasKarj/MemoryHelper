package com.example.muistikaveri

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muistikaveri.ui.theme.MuistikaveriTheme

class MainActivity : ComponentActivity() {
    private val routineViewModel: RoutineViewModel by viewModels()
    private val caregiverViewModel: CaregiverViewModel by viewModels()
    private val wellnessViewModel: WellnessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MuistikaveriTheme {
                var userRole by remember { mutableStateOf<String?>(null) }
                var isWellnessChatOpen by remember { mutableStateOf(false) }

                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF7F5F0)) {
                    when (userRole) {
                        null -> {
                            RoleSelectionScreen(onRoleSelected = { selectedRole ->
                                if (selectedRole == "patient") {
                                    startPatientServices()
                                }
                                userRole = selectedRole
                            })
                        }
                        "patient" -> {
                            if (isWellnessChatOpen) {
                                WellnessConversationScreen(
                                    viewModel = wellnessViewModel,
                                    onBack = { isWellnessChatOpen = false }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    PatientHomeScreen(
                                        routines = routineViewModel.routines,
                                        onRoutineClick = { routine -> routineViewModel.toggleRoutineStatus(routine) },
                                        onWellnessClick = { isWellnessChatOpen = true }
                                    )
                                }
                            }
                        }
                        "caregiver" -> {
                            CaregiverHomeScreen(
                                patient = caregiverViewModel.patientData,
                                routines = routineViewModel.routines,
                                onBack = { userRole = null }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startPatientServices() {
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        androidx.core.app.ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 102)

        val serviceIntent = Intent(this, SafetyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tervetuloa\nMuistikaveriin",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            color = Color(0xFF0A1F3D)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = { onRoleSelected("patient") },
            modifier = Modifier.fillMaxWidth().height(80.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F4E8F))
        ) {
            Icon(Icons.Default.Person, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Olen potilas", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { onRoleSelected("caregiver") },
            modifier = Modifier.fillMaxWidth().height(80.dp),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF0F4E8F))
        ) {
            Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF0F4E8F))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Olen omainen", fontSize = 20.sp, color = Color(0xFF0F4E8F))
        }
    }
}

@Composable
fun PatientHomeScreen(
    routines: List<Routine>,
    onRoutineClick: (Routine) -> Unit,
    onWellnessClick: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedRoutine by remember { mutableStateOf<Routine?>(null) }

    if (showConfirmDialog && selectedRoutine != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(text = "Muutetaanko tila?") },
            text = { Text(text = "Haluatko varmasti merkitä tehtävän '${selectedRoutine?.title}' tekemättömäksi?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedRoutine?.let { onRoutineClick(it) }
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F4E8F))
                ) {
                    Text("Kyllä")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Peruuta")
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        Text(text = "Hyvää\nhuomenta!", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1F3D))
        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
            items(routines) { routine ->
                val icon = when (routine.type) {
                    "medicine" -> Icons.Default.Medication
                    "food" -> Icons.Default.Restaurant
                    else -> Icons.Default.CheckCircle
                }
                RoutineItem(
                    icon = icon,
                    title = routine.title,
                    isCompleted = routine.isCompleted,
                    onClick = {
                        if (routine.isCompleted) {
                            selectedRoutine = routine
                            showConfirmDialog = true
                        } else {
                            onRoutineClick(routine)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onWellnessClick,
            modifier = Modifier.fillMaxWidth().height(72.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F4E8F))
        ) {
            Text(text = "Vointi", fontSize = 24.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineItem(icon: ImageVector, title: String, isCompleted: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isCompleted) Color(0xFFE8F5E9) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else icon,
                contentDescription = null,
                tint = if (isCompleted) Color(0xFF2E7D32) else Color(0xFF0A1F3D),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(text = title, fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0A1F3D))
                Text(text = if (isCompleted) "Valmis" else "Ei valmis", fontSize = 18.sp, color = if (isCompleted) Color(0xFF2E7D32) else Color.Gray)
            }
        }
    }
}

@Composable
fun WellnessConversationScreen(viewModel: WellnessViewModel, onBack: () -> Unit) {
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F5F0)).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF0A1F3D))
            }
            Text("Mitä kuuluu?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1F3D))
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Lisätään tyyppi ChatMessage selkeyden vuoksi
            items(viewModel.messages) { message: ChatMessage ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        color = if (message.isUser) Color(0xFF0F4E8F) else Color.White,
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = message.text, // Nyt 'text' löytyy
                            modifier = Modifier.padding(12.dp),
                            color = if (message.isUser) Color.White else Color(0xFF0A1F3D),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp).navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Kirjoita tähän...") },
                    // Lisätty näppäimistöasetukset varmuuden vuoksi:
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                        capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier.background(Color(0xFF0F4E8F), CircleShape)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}