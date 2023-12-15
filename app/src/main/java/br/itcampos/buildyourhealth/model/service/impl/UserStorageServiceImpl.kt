package br.itcampos.buildyourhealth.model.service.impl

import br.itcampos.buildyourhealth.model.User
import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.UserStorageService
import br.itcampos.buildyourhealth.model.service.module.IoDispatcher
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserStorageServiceImpl @Inject constructor(
    private val buildYourHealthAppDb: FirebaseFirestore,
    private val auth: AccountService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserStorageService {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val user: Flow<User?>
        get() = auth.currentUser.flatMapLatest { user ->
            buildYourHealthAppDb.collection(USER_COLLECTION).document(user.uid).dataObjects()
        }

    override suspend fun getUser(userId: String): User? =
        buildYourHealthAppDb.collection(USER_COLLECTION).document(userId).get().await().toObject()


    override suspend fun saveUser(uid: String, name: String, email: String) {
        val user = User(uid, name, email)
        buildYourHealthAppDb.collection(USER_COLLECTION).document(uid).set(user).await()
    }

    companion object {
        private const val USER_COLLECTION = "users"
        private const val USER_UID_FIELD = "uid"
    }
}