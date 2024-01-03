package br.itcampos.buildyourhealth.ui.state

import br.itcampos.buildyourhealth.model.Training

data class TrainingScreenUiState(
    val isLoading: Boolean = false,
    val trainings: List<Training> = emptyList(),
    val trainingToBeUpdated: Training? = null,
    val isShowUpdateTrainingDialog: Boolean = false,
    val currentTextFieldName: String = "",
    val currentTextFieldDescription: String = "",
    val currentTextFieldDate: String = "",
    val trainingDetails: Training? = null
)
