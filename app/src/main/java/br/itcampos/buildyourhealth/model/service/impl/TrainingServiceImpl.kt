package br.itcampos.buildyourhealth.model.service.impl

import android.util.Log
import br.itcampos.buildyourhealth.model.Exercise
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.TrainingService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TrainingServiceImpl @Inject constructor(
    private val accountService: AccountService,
    private val firestore: FirebaseFirestore,
    private val scope: CoroutineScope
) : TrainingService {

    override val trainings: Flow<List<Training>>
        get() = accountService.currentUser.flatMapLatest { user ->
            if (user != null) {
                val userId = user.uid
                Log.d(TAG, "Fetching trainings for user: $userId")

                flow {
                    val snapshot = firestore.collection(TRAINING_COLLECTION)
                        .whereEqualTo(USER_ID_FIELD, userId)
                        .get()
                        .await()

                    val trainings = snapshot.toObjects(Training::class.java)
                    emit(trainings)
                }
                    .onStart { Log.d(TAG, "Fetching trainings started") }
                    .onCompletion { Log.d(TAG, "Fetching trainings completed") }
            } else {
                flow {
                    emit(emptyList<Training>())
                }
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )


    override suspend fun getTrainingById(trainingId: String): Training? =
        firestore.collection(TRAINING_COLLECTION).document(trainingId).get().await().toObject()


    override suspend fun addTraining(training: Training) {
        firestore.collection(TRAINING_COLLECTION).add(training).await().id
    }

    override suspend fun updateTraining(training: Training) {
        try {
            if (training.id.isNotEmpty()) {
                firestore.collection(TRAINING_COLLECTION)
                    .document(training.id)
                    .set(training)
                    .await()
            } else {
                Log.e(TAG, "O ID do treino está vazio, não pode atualizar")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar Training: ${e.localizedMessage}")
        }
    }

    override suspend fun deleteTraining(trainingId: String) {
        try {
            firestore.collection(TRAINING_COLLECTION)
                .document(trainingId)
                .delete()
                .await()

            removeExerciseReferencesFromTraining(trainingId)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar Training: ${e.localizedMessage}")
        }
    }

    private suspend fun removeExerciseReferencesFromTraining(trainingId: String) {
        try {
            val exercises = firestore.collection(EXERCISE_COLLECTION)
                .whereEqualTo(TRAINING_ID, trainingId)
                .get()
                .await()
                .toObjects(Exercise::class.java)

            for (exercise in exercises) {
                firestore.collection(EXERCISE_COLLECTION)
                    .document(exercise.id)
                    .update(TRAINING_ID, null)
                    .await()
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Erro ao remover referências de exercícios do Training: ${e.localizedMessage}"
            )
        }
    }

    companion object {
        private const val EXERCISE_COLLECTION = "exercises"
        private const val TRAINING_COLLECTION = "trainings"
        private const val TRAINING_ID = "id"
        private const val USER_ID_FIELD = "userId"
        private const val TAG = "ExerciseServiceImpl"
    }
}