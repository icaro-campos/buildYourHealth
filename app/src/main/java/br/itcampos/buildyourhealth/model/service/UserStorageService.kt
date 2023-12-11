package br.itcampos.buildyourhealth.model.service

import br.itcampos.buildyourhealth.model.User
import kotlinx.coroutines.flow.Flow

interface UserStorageService {
    val user: Flow<User?>

    suspend fun getUser(userId: String): User?
    suspend fun saveUser(uid: String, name: String, email: String)
}