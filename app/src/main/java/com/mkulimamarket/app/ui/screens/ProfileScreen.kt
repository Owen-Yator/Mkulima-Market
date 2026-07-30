package com.mkulimamarket.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.ui.viewmodel.ProfileViewModel

private val CanopyGreen = Color(0xFF1B4332)
private val Ivory = Color(0xFFFBF7EE)
private val Charcoal = Color(0xFF1F2620)
private val MutedText = Color(0xFF6B7268)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val userState by viewModel.userState.collectAsState()
    val context = LocalContext.current
    var showPersonalDetailsSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ivory)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = CanopyGreen
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Info Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(CanopyGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (userState.isLoading) {
                    CircularProgressIndicator(
                        color = CanopyGreen,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Column {
                        Text(
                            text = userState.name.ifBlank { "Mkulima User" },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Charcoal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userState.email.ifBlank { "No email set" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ACCOUNT SETTINGS",
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MutedText
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 1. Personal Details Card (Opens Bottom Sheet)
        ProfileOptionItem(
            title = "Personal Details",
            icon = Icons.Default.AccountCircle,
            onClick = {
                showPersonalDetailsSheet = true
            }
        )

        // 2. Preferred Markets Card (Shows Toast Feedback)
        ProfileOptionItem(
            title = "My Preferred Markets",
            icon = Icons.Default.Place,
            onClick = {
                Toast.makeText(context, "Preferred Markets selection coming soon!", Toast.LENGTH_SHORT).show()
            }
        )

        // 3. Notifications & Price Alerts Card (Shows Toast Feedback)
        ProfileOptionItem(
            title = "Notifications & Price Alerts",
            icon = Icons.Default.Notifications,
            onClick = {
                Toast.makeText(context, "Price Alerts feature coming soon!", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Sign Out Button
        Button(
            onClick = {
                viewModel.signOut { onSignOut() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F)
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Modal Bottom Sheet displaying Personal Details
    if (showPersonalDetailsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPersonalDetailsSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Personal Details",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CanopyGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                DetailRow(label = "Full Name", value = userState.name.ifBlank { "Not provided" })
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

                DetailRow(label = "Email Address", value = userState.email.ifBlank { "Not provided" })
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

                DetailRow(label = "Account Role", value = "Trader / Farmer")
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

                DetailRow(label = "Primary Region", value = "Kenya")

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showPersonalDetailsSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CanopyGreen)
                ) {
                    Text("Close")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MutedText
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Charcoal
        )
    }
}

@Composable
private fun ProfileOptionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CanopyGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Charcoal,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MutedText
            )
        }
    }
}