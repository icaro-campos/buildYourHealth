package br.itcampos.buildyourhealth.screens.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.itcampos.buildyourhealth.commom.EmptyScreen
import br.itcampos.buildyourhealth.commom.convertDateFormat
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.navigation.Routes.SIDE_EFFECTS_KEY
import br.itcampos.buildyourhealth.navigation.Routes.TRAINING_SCREEN
import br.itcampos.buildyourhealth.ui.events.HomeScreenUiEvents
import br.itcampos.buildyourhealth.ui.side_effects.ScreenUiSideEffect
import br.itcampos.buildyourhealth.ui.theme.Primary
import kotlinx.coroutines.flow.onEach
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
    val uiState = viewModel.state.collectAsState().value

    val effects = viewModel.effect

    val events = viewModel

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = SIDE_EFFECTS_KEY) {
        effects.onEach { effect ->
            when (effect) {
                is ScreenUiSideEffect.ShowSnackbarMessage -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                        actionLabel = "DISMISS"
                    )
                }
            }
        }
    }

    Log.d("HomeScreenTrainings", "Trainings received: $uiState")

    HomeScreenContent(
        trainings = uiState.trainings,
        onTrainingAddClick = viewModel::onAddNewTrainingClick,
        onDeleteTrainingClick = events,
        //onTrainingClick = { },
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
    onDeleteTrainingClick: HomeViewModel,
    //onTrainingClick: ((String) -> Unit, Training, String) -> Unit,
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
                    event = onDeleteTrainingClick,

                    //onTrainingClick = onTrainingClick,
                    openScreen = openScreen
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onTrainingAddClick(openScreen) },
                modifier = modifier.padding(16.dp),
                backgroundColor = Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Botão adicionar",
                    tint = Color.White
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingList(
    trainings: List<Training>,
    event: HomeViewModel,
    //onTrainingClick: ((String) -> Unit, Training, String) -> Unit,
    openScreen: (String) -> Unit
) {
    Log.d("HomeScreenTrainings", "Trainings: $trainings, Number of trainings: ${trainings.size}")

    if (trainings.isEmpty()) {
        EmptyScreen(error = null)
    } else {
        LazyColumn {
            items(trainings) { training ->
                TrainingListItem(
                    training = training,
                    deleteTraining = { trainingId ->
                        event.sendEvent(HomeScreenUiEvents.DeleteTraining(trainingId))
                    },
                    editTraining = { training ->
                        /*event.sendEvent(
                            HomeScreenUiEvents.UpdateTraining(
                                openScreen = TRAINING_SCREEN,
                                training = training
                            )*/
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingListItem(
    deleteTraining: (String) -> Unit,
    editTraining: (Training) -> Unit,
    training: Training
) {
    Log.d("HomeScreenTrainings", "Training item: $training")

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
    val date = LocalDate.parse(training.date, formatter)
    val dateString = convertDateFormat(date.toString())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = training.name, style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = training.description, style = MaterialTheme.typography.body2)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateString,
                    style = MaterialTheme.typography.caption
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = { deleteTraining(training.trainingId) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Botão de deletar",
                    )
                }

                IconButton(
                    onClick = { editTraining(training) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Botão de editar",
                    )
                }

            }
        }
    }
}