package br.itcampos.buildyourhealth.screens.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
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
import androidx.navigation.compose.rememberNavController
import br.itcampos.buildyourhealth.commom.AddTrainingComposable
import br.itcampos.buildyourhealth.commom.CircularProgressComposable
import br.itcampos.buildyourhealth.commom.EmptyScreen
import br.itcampos.buildyourhealth.commom.convertDateFormat
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.navigation.Routes.SIDE_EFFECTS_KEY
import br.itcampos.buildyourhealth.navigation.Routes.TRAINING_ID
import br.itcampos.buildyourhealth.navigation.Routes.TRAINING_SCREEN
import br.itcampos.buildyourhealth.ui.events.TrainingScreenUiEvents
import br.itcampos.buildyourhealth.ui.side_effects.ScreenUiSideEffect
import br.itcampos.buildyourhealth.ui.state.TrainingScreenUiState
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

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = SIDE_EFFECTS_KEY) {
        viewModel.sendEvent(TrainingScreenUiEvents.GetTrainings)
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
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        events = viewModel,
        trainings = uiState.trainings,
        onTrainingClick = viewModel::onTrainingClick,
        openScreen = openScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreenContent(
    snackbarHostState: SnackbarHostState,
    uiState: TrainingScreenUiState,
    events: HomeViewModel,
    modifier: Modifier = Modifier,
    trainings: List<Training>,
    onTrainingClick: ((String) -> Unit, Training) -> Unit,
    openScreen: (String) -> Unit
) {
    if (uiState.isShowAddTrainingDialog) {
        AddTrainingComposable(
            uiState = uiState,
            setTrainingName = { name ->
                events.sendEvent(
                    event = TrainingScreenUiEvents.OnChangeTrainingName(name = name)
                )
            },
            setTrainingDescription = { description ->
                events.sendEvent(
                    event = TrainingScreenUiEvents.OnChangeTrainingDescription(description = description)
                )
            },
            setTrainingDate = { date ->
                events.sendEvent(
                    event = TrainingScreenUiEvents.OnChangeTrainingDate(date = date)
                )
            },
            addTraining = {
                events.sendEvent(
                    event = TrainingScreenUiEvents.AddTraining(
                        name = uiState.currentTextFieldName,
                        description = uiState.currentTextFieldDescription,
                        date = uiState.currentTextFieldDate
                    )
                )
            },
            closeDialog = {
                events.sendEvent(
                    event = TrainingScreenUiEvents.OnChangeAddTrainingDialogState(show = false)
                )
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                elevation = 1.dp,
                modifier = Modifier.wrapContentSize(Alignment.TopEnd),
                title = { Text("Treinos", color = Primary) },
                backgroundColor = Color.Unspecified
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    events.sendEvent(
                        event = TrainingScreenUiEvents.OnChangeAddTrainingDialogState(
                            show = true
                        )
                    )
                },
                modifier = modifier.padding(16.dp),
                backgroundColor = Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Botão adicionar",
                    tint = Color.White
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            when {
                uiState.isLoading -> CircularProgressComposable()
            }

            if (trainings.isEmpty()) {
                EmptyScreen(error = null)
            } else {
                LazyColumn {
                    items(trainings) { training ->
                        TrainingListItem(
                            training = training,
                            deleteTraining = { trainingId ->
                                events.sendEvent(
                                    event = TrainingScreenUiEvents.DeleteTraining(
                                        trainingId = trainingId
                                    )
                                )
                            },
                            onTrainingClick = { onTrainingClick(openScreen, training) }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingListItem(
    deleteTraining: (String) -> Unit,
    onTrainingClick: (String) -> Unit,
    training: Training,
) {
    Log.d("HomeScreenTrainings", "Training item: $training")

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
    val date = LocalDate.parse(training.date, formatter)
    val dateString = convertDateFormat(date.toString())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onTrainingClick(training.trainingId) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = training.name, style = MaterialTheme.typography.h6, color = Primary)

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
                        tint = Primary,
                        contentDescription = "Botão de deletar",
                    )
                }
            }
        }
    }
}