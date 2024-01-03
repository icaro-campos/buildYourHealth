package br.itcampos.buildyourhealth.ui.events

sealed class HomeScreenUiEvents {
    object GetTrainings : HomeScreenUiEvents()

    data class AddTraining(val name: String, val description: String, val date: String) :
        HomeScreenUiEvents()

    data class OnChangeTrainingName(val name: String) : HomeScreenUiEvents()

    data class OnChangeTrainingDescription(val description: String) : HomeScreenUiEvents()

    data class OnChangeTrainingDate(val date: String) : HomeScreenUiEvents()

    data class OnChangeAddTrainingDialogState(val show: Boolean) : HomeScreenUiEvents()

    data class DeleteTraining(val trainingId: String) : HomeScreenUiEvents()
}
