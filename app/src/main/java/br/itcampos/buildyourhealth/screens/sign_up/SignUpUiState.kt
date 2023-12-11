package br.itcampos.buildyourhealth.screens.sign_up

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    var isLoading: Boolean = false
)
