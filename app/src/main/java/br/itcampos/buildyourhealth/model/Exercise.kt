package br.itcampos.buildyourhealth.model

data class Exercise(
    var id: String = "",
    var userId: String = "",
    var trainingId: String = "",
    val name: String = "",
    val observations: String = "",
    val imageUrl: String = ""
)
