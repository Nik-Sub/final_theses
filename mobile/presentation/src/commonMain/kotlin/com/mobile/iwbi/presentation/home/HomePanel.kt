package com.mobile.iwbi.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iwbi.domain.shopping.ShoppingItem
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomePanel() {
    val viewModel = koinViewModel<HomePanelViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.helloWorld()
            }) {
                Text("+", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Active Shopping List",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.notes.size) { index ->
                    ShoppingListItem(uiState.notes[index], onClick = {
                        viewModel.toggleItem(index)
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Templates",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.templates.size) { index ->
                    TemplateItem(
                        uiState.templates[index],
                        onClick = { viewModel.addTemplate(uiState.templates[index]) })
                }
            }
        }
    }
}


@Composable
fun ShoppingListItem(item: ShoppingItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                if (item.isChecked) Color.Green.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            fontWeight = if (item.isChecked) FontWeight.Light else FontWeight.Bold,
            color = if (item.isChecked) Color.Gray else Color.Black
        )
    }
}

@Composable
fun TemplateItem(template: List<ShoppingItem>, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Blue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Template: ${template.joinToString { it.name }}",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}