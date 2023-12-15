package br.itcampos.buildyourhealth.model.service.module

import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.model.service.UserStorageService
import br.itcampos.buildyourhealth.model.service.impl.AccountServiceImpl
import br.itcampos.buildyourhealth.model.service.impl.TrainingServiceImpl
import br.itcampos.buildyourhealth.model.service.impl.UserStorageServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideAccountService(
        auth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AccountService {
        return AccountServiceImpl(
            auth = auth,
            buildYourHealthAppDb = firebaseFirestore,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideTrainingService(
        accountService: AccountService,
        firebaseFirestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): TrainingService {
        return TrainingServiceImpl(
            user = accountService,
            buildYourHealthAppDb = firebaseFirestore,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideUserService(
        accountService: AccountService,
        firebaseFirestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): UserStorageService {
        return UserStorageServiceImpl(
            auth = accountService,
            buildYourHealthAppDb = firebaseFirestore,
            ioDispatcher = ioDispatcher
        )
    }

}