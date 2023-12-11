package br.itcampos.buildyourhealth.model.service

import br.itcampos.buildyourhealth.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseService {

    val exercices: Flow<List<Exercise>>

    suspend fun getExercisesForTraining(trainingId: String): List<Exercise>
    suspend fun addExercise(trainingId: String, exercise: Exercise)
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exerciseId: String)
}