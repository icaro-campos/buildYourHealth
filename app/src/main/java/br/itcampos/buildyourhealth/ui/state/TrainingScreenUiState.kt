package br.itcampos.buildyourhealth.ui.state

import br.itcampos.buildyourhealth.model.Training

data class TrainingScreenUiState(
    val isLoading: Boolean = false,
    val trainings: List<Training> = emptyList(),
    val errorMessage: String? = null,
    val trainingToBeUpdated: Training? = null,
    val currentTextFieldName: String = "",
    val currentTextFieldDescription: String = "",
    val currentTextFieldDate: String = ""
)