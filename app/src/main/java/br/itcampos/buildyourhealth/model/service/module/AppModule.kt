package br.itcampos.buildyourhealth.model.service.module

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides fun auth() : FirebaseAuth = Firebase.auth

    @Provides fun firestore() : FirebaseFirestore = Firebase.firestore

    @Provides @IoDispatcher fun dispatcher() : CoroutineDispatcher = Dispatchers.IO

}

@Retention
@Qualifier
annotation class IoDispatcher