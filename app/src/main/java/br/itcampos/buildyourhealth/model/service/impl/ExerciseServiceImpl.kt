package br.itcampos.buildyourhealth.model.service.impl

import android.util.Log
import br.itcampos.buildyourhealth.model.Exercise
import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.ExerciseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExerciseServiceImpl @Inject constructor(
    private val accountService: AccountService,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ExerciseService {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val exercices: Flow<List<Exercise>>
        get() = accountService.currentUser.flatMapLatest { user ->
            firestore.collection(EXERCISE_COLLECTION).whereEqualTo(USER_ID_FIELD, user.uid)
                .dataObjects()
        }


    override suspend fun getExercisesForTraining(trainingId: String): List<Exercise> =
        firestore.collection(EXERCISE_COLLECTION)
            .whereEqualTo(TRAINING_ID, trainingId)
            .get()
            .await()
            .toObjects(Exercise::class.java)

    override suspend fun addExercise(trainingId: String, exercise: Exercise) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            exercise.userId = currentUserId
            exercise.trainingId = trainingId
            val exerciseDocRef = firestore.collection(EXERCISE_COLLECTION).add(exercise).await()
            firestore.collection(TRAINING_COLLECTION).document(trainingId).update(
                EXERCISE_COLLECTION, FieldValue.arrayUnion(exerciseDocRef.id)
            ).await()
        } else {
            Log.e(TAG, "Usuário não autenticado")
        }
    }

    override suspend fun updateExercise(exercise: Exercise) {
        try {
            if (exercise.id.isNotEmpty()) {
                firestore.collection(EXERCISE_COLLECTION)
                    .document(exercise.id)
                    .set(exercise)
                    .await()
            } else {
                Log.e(TAG, "O ID do exercício está vazio, não pode atualizar")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar Exercise: ${e.localizedMessage}")
        }
    }

    override suspend fun deleteExercise(exerciseId: String) {
        try {
            firestore.collection(EXERCISE_COLLECTION)
                .document(exerciseId)
                .delete()
                .await()

            removeExerciseReferenceFromTraining(exerciseId)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar Exercise: ${e.localizedMessage}")
        }
    }

    private suspend fun removeExerciseReferenceFromTraining(exerciseId: String) {
        try {
            val querySnapshot = firestore.collection(TRAINING_COLLECTION)
                .whereArrayContains(EXERCISE_COLLECTION, exerciseId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val trainingId = document.id
                firestore.collection(TRAINING_COLLECTION)
                    .document(trainingId)
                    .update(EXERCISE_COLLECTION, FieldValue.arrayRemove(exerciseId))
                    .await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao remover a referência de Training ${e.localizedMessage}")
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