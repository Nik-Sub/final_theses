package com.mobile.iwbi.presentation.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardPadding
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfilePanel(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(StandardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
    ) {
        // Header
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        currentUser?.let { user ->
            // User info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m),
                elevation = CardDefaults.cardElevation(defaultElevation = IWBIDesignTokens.elevation_card)
            ) {
                Column(
                    modifier = Modifier.padding(StandardPadding),
                    verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
                ) {
                    // Profile icon and email
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(IWBIDesignTokens.icon_size_large),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Column {
                            user.email?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "Account Email",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Divider()

                    // Email verification status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
                    ) {
                        Icon(
                            imageVector = if (user.isEmailVerified) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (user.isEmailVerified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )

                        Column {
                            Text(
                                text = if (user.isEmailVerified) "Email Verified" else "Email Not Verified",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (user.isEmailVerified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (user.isEmailVerified) "Your email has been verified" else "Please verify your email address",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sign out button
        Button(
            onClick = { viewModel.signOut() },
            modifier = Modifier
                .fillMaxWidth()
                .height(IWBIDesignTokens.button_height),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(IWBIDesignTokens.icon_size_small)
            )
            Spacer(modifier = Modifier.width(IWBIDesignTokens.space_s))
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
