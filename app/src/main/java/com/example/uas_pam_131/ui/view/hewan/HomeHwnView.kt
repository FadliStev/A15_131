package com.example.uas_pam_131.ui.view.hewan

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uas_pam_131.R
import com.example.uas_pam_131.model.Hewan
import com.example.uas_pam_131.model.Petugas
import com.example.uas_pam_131.ui.customwidget.CostumeTopAppBar
import com.example.uas_pam_131.ui.navigation.DestinasiHomeHwn
import com.example.uas_pam_131.ui.navigation.DestinasiHomePtgs
import com.example.uas_pam_131.ui.viewmodel.PenyediaViewModel
import com.example.uas_pam_131.ui.viewmodel.hewan.HomeHwnUiState
import com.example.uas_pam_131.ui.viewmodel.hewan.HomeHwnViewModel
import com.example.uas_pam_131.ui.viewmodel.petugas.HomePtgsUiState
import com.example.uas_pam_131.ui.viewmodel.petugas.HomePtgsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenHwn(
    navigateToltemEntry: ()-> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (Int) -> Unit = {},
    viewModel: HomeHwnViewModel = viewModel(factory = PenyediaViewModel.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CostumeTopAppBar(
                title = DestinasiHomeHwn.titleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getHwn()
                },
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToltemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Hewan")
            }
        },
    ) {innerPadding ->
        HomeStatusHwn(
            homeHwnUiState = viewModel.hwnUiState,
            retryAction = { viewModel.getHwn() }, modifier = Modifier.padding(innerPadding),
            onDetailClick = onDetailClick,
            onDeleteClick = {
                viewModel.deleteHwn(it.id_hewan)
                viewModel.getHwn()
            }
        )

    }
}

@Composable
fun HomeStatusHwn(
    homeHwnUiState: HomeHwnUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Hewan) -> Unit = {},
    onDetailClick: (Int) -> Unit
){
    when (homeHwnUiState){
        is HomeHwnUiState.Loading -> OnLoading(modifier = modifier.fillMaxSize())

        is HomeHwnUiState.Success ->
            if (homeHwnUiState.hewan.isEmpty()){
                return Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(text = "Tidak ada data Hewan")
                }
            }else{
                HwnLayout(
                    hewan = homeHwnUiState.hewan,
                    modifier = modifier.fillMaxWidth(),
                    onDetailClick = {
                        onDetailClick(it.id_hewan)
                    },
                    onDeleteClick = {
                        onDeleteClick(it)
                    }
                )
            }
        is HomeHwnUiState.Error -> OnError(retryAction, modifier = modifier.fillMaxSize())
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier){
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun OnError(retryAction: () -> Unit, modifier: Modifier = Modifier){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.network_error), contentDescription = "")
        Text(text = stringResource(id = R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(id = R.string.retry))
        }
    }
}

@Composable
fun HwnLayout(
    hewan: List<Hewan>,
    modifier: Modifier = Modifier,
    onDetailClick: (Hewan) -> Unit,
    onDeleteClick: (Hewan) -> Unit = {}
){
    var searchQuery by remember { mutableStateOf("") }

    // Filter daftar hewan berdasarkan query pencarian
    val filteredHewanList = hewan.filter {
        it.nama_hewan.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        // Input pencarian
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Cari Hewan")},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredHewanList){ hewan ->
                HwnCard(
                    hewan = hewan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDetailClick(hewan) },
                    onDeleteClick = {
                        onDeleteClick(hewan)
                    }
                )
            }
        }
    }

}

@Composable
fun HwnCard(
    hewan: Hewan,
    modifier: Modifier = Modifier,
    onDeleteClick:(Hewan) -> Unit = {}
){

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = hewan.nama_hewan,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.weight(1f))
                IconButton(onClick = {onDeleteClick(hewan)}) {
                    Icon(imageVector = Icons.Default.Delete,
                        contentDescription = null)
                }


            }
            Text(
                text = hewan.populasi,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = hewan.zona_wilayah,
                style = MaterialTheme.typography.titleMedium
            )

        }
    }
}