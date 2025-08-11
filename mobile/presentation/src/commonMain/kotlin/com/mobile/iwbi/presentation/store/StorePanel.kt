package com.mobile.iwbi.presentation.store

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobile.iwbi.domain.store.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorePanel(
    stores: List<Store>, // List of store names
    onStoreSelected: (Store) -> Unit // Callback when a store is selected
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stores") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Select a store:", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(stores.size) { index ->
                    Button(
                        onClick = { onStoreSelected(stores[index]) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stores[index].name)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}