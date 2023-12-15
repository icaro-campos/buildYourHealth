package br.itcampos.buildyourhealth.ui.side_effects

sealed class ScreenUiSideEffect {
    data class ShowSnackbarMessage(val message: String) : ScreenUiSideEffect()
}

