package br.itcampos.buildyourhealth.ui.events

import br.itcampos.buildyourhealth.model.Training

sealed class HomeScreenUiEvents {
    object GetTrainings : HomeScreenUiEvents()

    data class DeleteTraining(val trainingId: String) : HomeScreenUiEvents()
}
