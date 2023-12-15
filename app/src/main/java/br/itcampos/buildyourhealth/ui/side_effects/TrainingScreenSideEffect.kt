package br.itcampos.buildyourhealth.ui.side_effects

sealed class TrainingScreenSideEffect {
    data class ShowSnackbarMessage(val message: String) : TrainingScreenSideEffect()
}
