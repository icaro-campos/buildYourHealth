package br.itcampos.buildyourhealth.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.itcampos.buildyourhealth.commom.Result
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.navigation.Routes.TRAINING_SCREEN
import br.itcampos.buildyourhealth.ui.events.HomeScreenUiEvents
import br.itcampos.buildyourhealth.ui.side_effects.ScreenUiSideEffect
import br.itcampos.buildyourhealth.ui.state.HomeScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trainingService: TrainingService
) : ViewModel() {

    private val _state: MutableStateFlow<HomeScreenUiState> =
        MutableStateFlow(HomeScreenUiState())
    val state: StateFlow<HomeScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<ScreenUiSideEffect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun sendEvent(event: HomeScreenUiEvents) {
        reduce(event = event, oldState = state.value)
    }

    private fun setState(newState: HomeScreenUiState) {
        _state.value = newState
    }

    private fun setEffects(builder: () -> ScreenUiSideEffect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun reduce(event: HomeScreenUiEvents, oldState: HomeScreenUiState) {
        when (event) {

            is HomeScreenUiEvents.OnChangeTrainingName -> {
                onChangeTrainingName(oldState = oldState, name = event.name)
            }

            is HomeScreenUiEvents.OnChangeTrainingDescription -> {
                onChangeTrainingDescription(oldState = oldState, description = event.description)
            }

            is HomeScreenUiEvents.OnChangeTrainingDate -> {
                onChangeTrainingDate(oldState = oldState, date = event.date)
            }

            is HomeScreenUiEvents.OnChangeAddTrainingDialogState -> {
                onChangeAddTrainingDialogState(
                    oldState = oldState,
                    isShow = event.show
                )
            }

            HomeScreenUiEvents.GetTrainings -> {
                getAllTasks(oldState = oldState)
            }

            is HomeScreenUiEvents.AddTraining -> {
                addTraining(
                    oldState = oldState,
                    name = event.name,
                    description = event.description,
                    date = event.date
                )
            }

            is HomeScreenUiEvents.DeleteTraining -> {
                deleteTraining(
                    oldState = oldState,
                    trainingId = event.trainingId
                )
            }
        }
    }

    private fun onChangeTrainingName(oldState: HomeScreenUiState, name: String) {
        setState(oldState.copy(currentTextFieldName = name))
    }

    private fun onChangeTrainingDescription(oldState: HomeScreenUiState, description: String) {
        setState(oldState.copy(currentTextFieldDescription = description))
    }

    private fun onChangeTrainingDate(oldState: HomeScreenUiState, date: String) {
        setState(oldState.copy(currentTextFieldDate = date))
    }

    private fun onChangeAddTrainingDialogState(oldState: HomeScreenUiState, isShow: Boolean) {
        setState(
            oldState.copy(
                isShowAddTrainingDialog = isShow
            )
        )
    }

    private fun getAllTasks(oldState: HomeScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))
            when (val result = trainingService.getAllTrainings()) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    val errorMessage = result.exception.message ?: "Um erro ao buscar os Treinos"
                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldState.copy(isLoading = false, trainings = result.data))
                }
            }
        }
    }

    private fun addTraining(
        oldState: HomeScreenUiState,
        name: String,
        description: String,
        date: String
    ) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result =
                trainingService.addTraining(name = name, description = description, date = date)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "Um erro ocorreu ao adicionar o Treino"

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldName = "",
                            currentTextFieldDescription = "",
                            currentTextFieldDate = ""
                        )
                    )

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = "Tarefa adicionanda com sucesso") }

                    sendEvent(HomeScreenUiEvents.GetTrainings)
                }
            }
        }
    }

    private fun deleteTraining(oldState: HomeScreenUiState, trainingId: String) {
        viewModelScope.launch {
            setState(
                oldState.copy(
                    isLoading = true
                )
            )

            when (val result = trainingService.deleteTraining(trainingId)) {
                is Result.Failure -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    val errorMessage =
                        result.exception.message ?: "Um erro ocorreu ao deletar a Tarefa"

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = "Tarefa deletada com sucesso") }

                    sendEvent(HomeScreenUiEvents.GetTrainings)
                }
            }
        }
    }

    fun onTrainingClick(openScreen: (String) -> Unit, training: Training) {
        openScreen("$TRAINING_SCREEN/${training.trainingId}")
    }


    companion object {
        private const val UTC = "UTC"
        private const val DATE_FORMAT = "EEE, d MMM yyyy"
    }
}