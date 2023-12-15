package br.itcampos.buildyourhealth.model.service

import br.itcampos.buildyourhealth.commom.Result
import br.itcampos.buildyourhealth.model.Training

interface TrainingService {
    suspend fun addTraining(name: String, description: String, date: String): Result<Unit>
    suspend fun getAllTrainings(): Result<List<Training>>
    suspend fun updateTraining(trainingId: String, name: String, description: String, date: String): Result<Unit>
    suspend fun deleteTraining(trainingId: String): Result<Unit>
}