package br.itcampos.buildyourhealth.model

import java.util.UUID

data class Training(
    var id: String = UUID.randomUUID().toString(),
    var userId: String = "",
    var name: String = "",
    var description: String = "",
    var date: String = ""
) {
    var exercises: List<Exercise> = mutableListOf()
}
