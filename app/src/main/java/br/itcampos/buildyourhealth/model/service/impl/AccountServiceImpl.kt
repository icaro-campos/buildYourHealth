package br.itcampos.buildyourhealth.model.service.impl

import android.util.Log
import br.itcampos.buildyourhealth.model.User
import br.itcampos.buildyourhealth.model.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val TAG = "AccountServiceImpl"

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AccountService {

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) } ?: User())
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun createAccount(email: String, password: String): String? {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult?.user?.uid
        } catch (e: Exception) {
            Log.d(TAG, "createAccount: Firebase Authentication failed: ${e.localizedMessage}")
            null
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}