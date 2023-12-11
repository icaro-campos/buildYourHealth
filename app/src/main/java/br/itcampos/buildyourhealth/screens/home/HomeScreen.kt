package br.itcampos.buildyourhealth.screens.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.itcampos.buildyourhealth.commom.EmptyScreen
import br.itcampos.buildyourhealth.model.Training
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    openScreen: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val trainings = viewModel.trainings.collectAsStateWithLifecycle(emptyList())

    Log.d("HomeScreenTrainings", "Trainings received: $trainings")

    HomeScreenContent(
        trainings = trainings.value,
        onTrainingAddClick = viewModel::onAddNewTrainingClick,
        onTrainingClick = viewModel::onTrainingClick,
        openScreen = openScreen
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    trainings: List<Training>,
    onTrainingAddClick: ((String) -> Unit) -> Unit,
    onTrainingClick: ((String) -> Unit, Training, String) -> Unit,
    openScreen: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                elevation = 1.dp,
                modifier = Modifier.wrapContentSize(Alignment.TopEnd),
                title = { Text("Treinos") },
                backgroundColor = Color.Unspecified
            )
        },
        content = {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                TrainingList(
                    trainings = trainings,
                    onTrainingClick = onTrainingClick,
                    openScreen = openScreen
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onTrainingAddClick(openScreen) },
                modifier = modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Botão adicionar"
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingList(
    trainings: List<Training>,
    onTrainingClick: ((String) -> Unit, Training, String) -> Unit,
    openScreen: (String) -> Unit
) {
    Log.d("TrainingList", "Trainings: $trainings, Number of trainings: ${trainings.size}")

    if (trainings.isEmpty()) {
        EmptyScreen(error = null)
    } else {
        LazyColumn {
            items(trainings, key = { it.id }) { trainingItem ->
                TrainingListItem(
                    training = trainingItem,
                    onTrainingClick = { action ->
                        onTrainingClick(
                            openScreen,
                            trainingItem,
                            action
                        )
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingListItem(
    training: Training,
    onTrainingClick: (String) -> Unit,
) {
    Log.d("HomeScreenTrainings", "Training item: $training")

    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)
    val date = LocalDate.parse(training.date.toString(), formatter)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrainingClick(training.id) }
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = training.name, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = training.description, style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                style = MaterialTheme.typography.caption
            )
        }
        Row(horizontalArrangement = Arrangement.End) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Botão de deletar",
                tint = Color.Red,
                modifier = Modifier
                    .clickable {

                    }
                    .padding(8.dp)
            )
        }
    }
}