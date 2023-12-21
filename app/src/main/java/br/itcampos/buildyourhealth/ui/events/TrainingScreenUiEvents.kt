package br.itcampos.buildyourhealth.ui.events

import br.itcampos.buildyourhealth.model.Training

sealed class TrainingScreenUiEvents {

    object GetTrainings : TrainingScreenUiEvents()

    data class AddTraining(val name: String, val description: String, val date: String) :
        TrainingScreenUiEvents()

    object UpdateTraining : TrainingScreenUiEvents()

    data class OnChangeTrainingName(val name: String) : TrainingScreenUiEvents()

    data class OnChangeTrainingDescription(val description: String) : TrainingScreenUiEvents()

    data class OnChangeTrainingDate(val date: String) : TrainingScreenUiEvents()

    data class OnChangeAddTrainingDialogState(val show: Boolean) : TrainingScreenUiEvents()

    data class OnChangeUpdateTrainingDialogState(val show: Boolean) : TrainingScreenUiEvents()

    data class SetTrainingToBeUpdated(val trainingToBeUpdated: Training) : TrainingScreenUiEvents()

    data class DeleteTraining(val trainingId: String) : TrainingScreenUiEvents()
    data class GetTrainingDetails(val trainingId: String) : TrainingScreenUiEvents()

}
