package br.itcampos.buildyourhealth.model.service

import br.itcampos.buildyourhealth.model.Training
import kotlinx.coroutines.flow.Flow

interface TrainingService {

    val trainings: Flow<List<Training>>
    suspend fun getTrainingById(trainingId: String): Training?
    suspend fun addTraining(training: Training)
    suspend fun updateTraining(training: Training)
    suspend fun deleteTraining(trainingId: String)
}