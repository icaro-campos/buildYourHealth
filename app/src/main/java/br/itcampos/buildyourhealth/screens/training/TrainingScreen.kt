package br.itcampos.buildyourhealth.screens.training

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.primarySurface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.itcampos.buildyourhealth.commom.BasicButton
import br.itcampos.buildyourhealth.commom.EmptyScreen
import br.itcampos.buildyourhealth.commom.GeneralBasicField
import br.itcampos.buildyourhealth.commom.RegularCardEditor
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.navigation.Routes.SIDE_EFFECTS_KEY
import br.itcampos.buildyourhealth.ui.events.TrainingScreenUiEvents
import br.itcampos.buildyourhealth.ui.side_effects.ScreenUiSideEffect
import br.itcampos.buildyourhealth.ui.state.TrainingScreenUiState
import br.itcampos.buildyourhealth.ui.theme.BuildYourHealthTheme
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
                        actionLabel = "DISMISS"
                    )
                }
            }
        }
    }

    LaunchedEffect(true) {
        uiState.training?.let { training ->
            viewModel.sendEvent(
                TrainingScreenUiEvents.GetTrainingDetails(trainingId = training.trainingId)
            )
        }
    }

    if (uiState.trainingDetails != null) {
        TrainingDetailsScreen(trainingDetails = uiState.trainingDetails) {
            viewModel.closeView(popUpScreen)
        }
    } else {
        EmptyScreen(error = null)
    }
}

@Composable
fun TrainingDetailsScreen(
    trainingDetails: Training?,
    onDoneClick: () -> Unit,
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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
                IconButton(onClick = onDoneClick) {//popUpScreen
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Botão voltar"
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(text = "${trainingDetails?.name}", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(24.dp))

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

@Composable
fun TrainingScreenContent(
    modifier: Modifier = Modifier,
    uiState: TrainingScreenUiState,
    setTrainingName: (String) -> Unit,
    setTrainingDescription: (String) -> Unit,
    setTrainingDateChange: (String) -> Unit,
    onDoneClick: () -> Unit,
    viewModel: TrainingViewModel
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            elevation = 0.dp,
            title = { Text(text = "Edite o seu treino") },
            backgroundColor = Color.Unspecified,
            modifier = Modifier.wrapContentSize(Alignment.TopEnd),
            navigationIcon = {
                IconButton(onClick = onDoneClick) {//popUpScreen
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Botão voltar"
                    )
                }
            }
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
            Text(
                text = uiState.currentTextFieldName
            )
            GeneralBasicField(
                value = uiState.currentTextFieldDescription,
                onNewValue = { description -> setTrainingDescription(description) },
                labelValue = "Descrição"
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
            CardEditors(setTrainingDateChange)

            Spacer(modifier = modifier.height(60.dp))

            BasicButton(
                value = "Salvar"
            ) {
                viewModel.sendEvent(
                    event = TrainingScreenUiEvents.AddTraining(
                        name = uiState.currentTextFieldName,
                        description = uiState.currentTextFieldDescription,
                        date = uiState.currentTextFieldDate
                    )
                )
                onDoneClick()
            }
        }
    }
}

@Composable
fun CardEditors(
    setTrainingDateChange: (String) -> Unit
) {
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    RegularCardEditor(
        "Data",
        Icons.Filled.DateRange,
        dateFormatter.format(selectedDate.time),
        Modifier.padding()
    ) {
        isDatePickerVisible = true
    }

    if (isDatePickerVisible) {
        ShowDatePickerDialog(selectedDate) { date ->
            selectedDate = date
            val formattedDate = dateFormatter.format(selectedDate.time)
            isDatePickerVisible = false

            setTrainingDateChange(formattedDate)
        }
    }
}

@Composable
fun ShowDatePickerDialog(initialDate: Calendar, onDateSelected: (Calendar) -> Unit) {
    val context = LocalContext.current
    val datePicker = remember { mutableStateOf(initialDate) }

    val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        datePicker.value.set(Calendar.YEAR, year)
        datePicker.value.set(Calendar.MONTH, month)
        datePicker.value.set(Calendar.DAY_OF_MONTH, day)
        onDateSelected(datePicker.value)
    }

    DatePickerDialog(
        context,
        onDateSetListener,
        datePicker.value.get(Calendar.YEAR),
        datePicker.value.get(Calendar.MONTH),
        datePicker.value.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Preview(showBackground = true)
@Composable
fun TrainingDetailsPreview() {
    BuildYourHealthTheme {
        val training = Training(
            name = "Treino Teste",
            description = "Descrição teste",
            date = "12/12/2023"
        )
        TrainingDetailsScreen(trainingDetails = training) {

        }
    }
}