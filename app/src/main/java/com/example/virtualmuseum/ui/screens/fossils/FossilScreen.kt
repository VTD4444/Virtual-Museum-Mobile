// In file: ui/screens/fossils/FossilsScreen.kt
package com.example.virtualmuseum.ui.screens.fossils

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // <-- CHANGE IMPORT
import androidx.compose.foundation.lazy.items // <-- Import for LazyColumn items
import androidx.compose.foundation.lazy.rememberLazyListState // <-- Change state if needed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.virtualmuseum.R
import com.example.virtualmuseum.ui.components.FossilCard
import com.example.virtualmuseum.ui.components.Spinner
import com.example.virtualmuseum.ui.navigation.Screen

@Composable
fun FossilsScreen(
    navController: NavController,
    vm: FossilsViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState() // Use LazyListState

    LaunchedEffect(key1 = state.error) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // --- CHANGE TO LazyColumn ---
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp), // Adjust padding
                verticalArrangement = Arrangement.spacedBy(16.dp) // Only vertical spacing needed
            ) {
                // --- Header (Phần Search) ---
                item { // No span needed for LazyColumn
                    SearchHeader(
                        state = state,
                        onEvent = vm::onEvent
                    )
                }

                // --- Danh sách Hóa thạch ---
                items(
                    items = state.fossils,
                    key = { fossil -> fossil.fossilId }
                ) { fossil ->
                    Log.d("FossilsScreen", "Displaying fossil: ${fossil.name}, URL: ${fossil.imageUrl}")
                    // --- UPDATE FossilCard CALL ---
                    FossilCard(
                        fossilName = fossil.name,
                        fossilOrigin = fossil.origin, // <-- PASS ORIGIN HERE
                        fossilImageUrl = fossil.imageUrl,
                        onClick = {
                            navController.navigate(Screen.FossilDetail.createRoute(fossil.fossilId))
                        }
                    )
                }
            }

            // --- Loading Spinner ---
            if (state.isLoading) {
                Spinner(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

// SearchHeader composable remains the same
@Composable
private fun SearchHeader(
    state: FossilsState,
    onEvent: (FossilsEvent) -> Unit
) {
    // ... (Keep existing SearchHeader code) ...
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp) // Keep bottom padding for separation
    ) {
        // Tiêu đề
        Text(
            text = stringResource(id = R.string.fossils_collection_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Ô tìm kiếm chính
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onEvent(FossilsEvent.OnSearchQueryChange(it)) },
            label = { Text(stringResource(id = R.string.search_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 2 ô filter phụ
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.period,
                onValueChange = { onEvent(FossilsEvent.OnPeriodChange(it)) },
                label = { Text(stringResource(id = R.string.period_placeholder)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = state.origin,
                onValueChange = { onEvent(FossilsEvent.OnOriginChange(it)) },
                label = { Text(stringResource(id = R.string.origin_placeholder)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Nút tìm kiếm
        Button(
            onClick = { onEvent(FossilsEvent.OnSearchClick) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.search_button))
        }
    }
}