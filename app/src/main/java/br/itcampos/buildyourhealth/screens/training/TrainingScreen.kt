package br.itcampos.buildyourhealth.screens.training

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import br.itcampos.buildyourhealth.commom.BasicButton
import br.itcampos.buildyourhealth.commom.GeneralBasicField
import br.itcampos.buildyourhealth.commom.RegularCardEditor
import br.itcampos.buildyourhealth.model.Training
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrainingScreen(
    popUpScreen: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel()
) {
    val training by viewModel.training
    val scaffoldState = rememberScaffoldState()

    TrainingScreenContent(
        training = training,
        scaffoldState = scaffoldState,
        onDoneClick = { viewModel.onDoneClick(popUpScreen) },
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onDateChange = viewModel::onDateChange,
        popUpScreen = popUpScreen
    )
}

@Composable
fun TrainingScreenContent(
    modifier: Modifier = Modifier,
    training: Training,
    scaffoldState: ScaffoldState,
    onDoneClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    popUpScreen: () -> Unit
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
                IconButton(onClick = popUpScreen) {
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
            GeneralBasicField(
                value = training.name,
                onNewValue = onNameChange,
                labelValue = "Nome"
            )
            GeneralBasicField(
                value = training.description,
                onNewValue = onDescriptionChange,
                labelValue = "Descrição"
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
            CardEditors(training, onDateChange)

            Spacer(modifier = modifier.height(60.dp))

            BasicButton(
                value = "Salvar"
            ) {
                onDoneClick()
            }
        }
    }
}

@Composable
fun CardEditors(
    training: Training,
    onDateChange: (Long) -> Unit
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
        showDatePickerDialog(selectedDate) { date ->
            selectedDate = date
            onDateChange(date.timeInMillis)
            isDatePickerVisible = false
        }
    }
}

@Composable
fun showDatePickerDialog(initialDate: Calendar, onDateSelected: (Calendar) -> Unit) {
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