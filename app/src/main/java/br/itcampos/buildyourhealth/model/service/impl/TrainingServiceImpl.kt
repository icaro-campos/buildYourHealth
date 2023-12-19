package br.itcampos.buildyourhealth.model.service.impl

import android.util.Log
import br.itcampos.buildyourhealth.commom.Result
import br.itcampos.buildyourhealth.commom.convertDateFormat
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.model.service.module.IoDispatcher
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class TrainingServiceImpl @Inject constructor(
    private val user: AccountService,
    private val buildYourHealthAppDb: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TrainingService {

    override suspend fun addTraining(
        name: String,
        description: String,
        date: String
    ): Result<Unit> {
        return try {
            val userId = user.currentUserId
            withContext(ioDispatcher) {
                val training = hashMapOf(
                    "userId" to userId,
                    "name" to name,
                    "description" to description,
                    "date" to date,
                )
                Log.d(TAG, "Dados convertidos")

                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    buildYourHealthAppDb.collection(TRAINING_COLLECTION)
                        .add(training)
                }
                Log.d(TAG, "Dados adicionados: $training")

                if (addTaskTimeout == null) {
                    Log.d(TAG, "Verifique a sua internet")

                    Result.Failure(IllegalStateException("Verifique a sua internet"))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d(TAG, "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun getAllTrainings(): Result<List<Training>> {
        return try {
            val userId = user.currentUserId
            withContext(ioDispatcher) {
                val fetchingTrainingsTimeout = withTimeoutOrNull(10000L) {
                    buildYourHealthAppDb.collection(TRAINING_COLLECTION)
                        .whereEqualTo(USER_ID_FIELD, userId)
                        .get()
                        .await()
                        .documents.map { document ->
                            Training(
                                trainingId = document.id,
                                userId = document.getString("userId") ?: "",
                                name = document.getString("name") ?: "",
                                description = document.getString("description") ?: "",
                                date = document.getString("date") ?: ""
                            )
                        }
                }
                if (fetchingTrainingsTimeout == null) {
                    Result.Failure(IllegalStateException("Verifique a sua internet."))
                }

                Result.Success(fetchingTrainingsTimeout?.toList() ?: emptyList())
            }
        } catch (e: Exception) {
            Result.Failure(exception = e)
        }
    }

    override suspend fun updateTraining(
        trainingId: String,
        name: String,
        description: String,
        date: String
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val trainingUpdated: Map<String, String> = hashMapOf(
                    "name" to name,
                    "description" to description,
                    "date" to date
                )

                val updateTrainingTimeout = withTimeoutOrNull(10000L) {
                    buildYourHealthAppDb.collection(TRAINING_COLLECTION)
                        .document(trainingId)
                        .update(trainingUpdated)
                }

                if (updateTrainingTimeout == null) {
                    Result.Failure(IllegalStateException("Verifique a sua internet."))
                }

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Failure(exception = e)
        }
    }


    override suspend fun deleteTraining(trainingId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val deleteTaskTimeout = withTimeoutOrNull(10000L) {
                    buildYourHealthAppDb.collection(TRAINING_COLLECTION)
                        .document(trainingId)
                        .delete()
                }

                if (deleteTaskTimeout == null) {
                    Result.Failure(IllegalStateException("Verifique a sua internet."))
                }

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Failure(exception = e)
        }
    }

    override suspend fun getTrainingDetails(trainingId: String): Result<Training> {
        return try {
            withContext(ioDispatcher) {
                val getTrainingDetailsTimeout = withTimeoutOrNull(10000L) {
                    val documentSnapshot = buildYourHealthAppDb.collection(TRAINING_COLLECTION)
                        .document(trainingId)
                        .get()
                        .await()

                    if (documentSnapshot.exists()) {
                        val training = Training(
                            trainingId = documentSnapshot.id,
                            userId = documentSnapshot.getString("userId") ?: "",
                            name = documentSnapshot.getString("name") ?: "",
                            description = documentSnapshot.getString("description") ?: "",
                            date = documentSnapshot.getString("date") ?: ""
                        )
                        Result.Success(training)
                    } else {
                        Result.Failure(IllegalStateException("Treino não encontrado."))
                    }
                }

                if (getTrainingDetailsTimeout == null) {
                    Result.Failure(IllegalStateException("Verifique a sua internet."))
                } else {
                    getTrainingDetailsTimeout
                }
            }
        } catch (e: Exception) {
            Result.Failure(exception = e)
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