package br.itcampos.buildyourhealth.ui.state

import br.itcampos.buildyourhealth.model.Training

data class HomeUiState(
    val isLoading: Boolean = false,
    val trainings: List<Training> = emptyList(),
    val errorMessage: String? = null
)