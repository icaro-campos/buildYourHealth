package br.itcampos.buildyourhealth.screens.training

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import br.itcampos.buildyourhealth.commom.CircularProgressComposable
import br.itcampos.buildyourhealth.commom.EmptyScreen
import br.itcampos.buildyourhealth.commom.UpdateTrainingComposable
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.navigation.Routes.SIDE_EFFECTS_KEY
import br.itcampos.buildyourhealth.ui.events.TrainingScreenUiEvents
import br.itcampos.buildyourhealth.ui.side_effects.ScreenUiSideEffect
import br.itcampos.buildyourhealth.ui.state.TrainingScreenUiState
import br.itcampos.buildyourhealth.ui.theme.BuildYourHealthTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


private const val TAG = "TrainingScreen"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingScreen(
    popUpScreen: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    val effects = viewModel.effect

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = SIDE_EFFECTS_KEY) {
        effects.onEach { effect ->
            when (effect) {
                is ScreenUiSideEffect.ShowSnackbarMessage -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                        actionLabel = "FECHAR"
                    )
                }
            }
        }.collect()
    }


    if (uiState.trainingDetails != null) {
        TrainingDetailsScreen(
            trainingDetails = uiState.trainingDetails,
            uiState = uiState,
            onDoneClick = { viewModel.closeView { popUpScreen() } },
            events = viewModel,
            snackbar = snackbarHostState
        )
    } else {
        EmptyScreen(error = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingDetailsScreen(
    snackbar: SnackbarHostState,
    trainingDetails: Training?,
    onDoneClick: () -> Unit,
    uiState: TrainingScreenUiState,
    events: TrainingViewModel
) {

    if (uiState.isShowUpdateTrainingDialog) {
        UpdateTrainingComposable(
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
            saveTraining = { events.sendEvent(event = TrainingScreenUiEvents.UpdateTraining) },
            closeDialog = {
                events.sendEvent(
                    event = TrainingScreenUiEvents.OnChangeUpdateTrainingDialogState(
                        show = false
                    )
                )
            },
            trainingDetails = uiState.trainingToBeUpdated

        )
    }

    Scaffold(
        modifier = Modifier.padding(16.dp),
        snackbarHost = {
            SnackbarHost(snackbar)
        },
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                title = {
                    Text(
                        text = "Detalhes do Treino",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = Color.Unspecified,
                modifier = Modifier.wrapContentSize(Alignment.TopEnd),
                navigationIcon = {
                    IconButton(onClick = onDoneClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Botão voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        events.sendEvent(
                            event = TrainingScreenUiEvents.OnChangeUpdateTrainingDialogState(
                                show = true
                            )
                        )
                        events.sendEvent(
                            event = TrainingScreenUiEvents.SetTrainingToBeUpdated(
                                trainingToBeUpdated = trainingDetails!!.copy(trainingDetails.trainingId)
                            )
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Botão Editar"
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            when {
                uiState.isLoading -> CircularProgressComposable()
            }
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray
                ),
                modifier = Modifier
            ) {
                Column(
                    modifier = Modifier.padding(8.dp, 16.dp)
                ) {
                    Text(text = "${trainingDetails?.name}", fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "${trainingDetails?.description}",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "${trainingDetails?.date}",
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}