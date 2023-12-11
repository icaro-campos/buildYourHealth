package br.itcampos.buildyourhealth.model.service

import br.itcampos.buildyourhealth.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean
    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun createAccount(email: String, password: String): String?
    suspend fun signOut()
}