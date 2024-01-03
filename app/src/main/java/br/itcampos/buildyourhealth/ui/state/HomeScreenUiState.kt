package br.itcampos.buildyourhealth.ui.state

import br.itcampos.buildyourhealth.model.Training

data class HomeScreenUiState(
    val isLoading: Boolean = false,
    val trainings: List<Training> = emptyList(),
    val isShowAddTrainingDialog: Boolean = false,
    val currentTextFieldName: String = "",
    val currentTextFieldDescription: String = "",
    val currentTextFieldDate: String = ""
)