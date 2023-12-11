package br.itcampos.buildyourhealth.model.service.module

import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.ExerciseService
import br.itcampos.buildyourhealth.model.service.LogService
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.model.service.UserStorageService
import br.itcampos.buildyourhealth.model.service.impl.AccountServiceImpl
import br.itcampos.buildyourhealth.model.service.impl.ExerciseServiceImpl
import br.itcampos.buildyourhealth.model.service.impl.LogServiceImpl
import br.itcampos.buildyourhealth.model.service.impl.TrainingServiceImpl
import br.itcampos.buildyourhealth.model.service.impl.UserStorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds abstract fun provideLogService(impl: LogServiceImpl): LogService

    @Binds abstract fun provideUserStorageService(impl: UserStorageServiceImpl): UserStorageService

    @Binds
    abstract fun provideTrainingService(
        impl: TrainingServiceImpl
    ): TrainingService

    companion object {
        @Singleton
        @Provides
        fun provideAppCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Default)
    }

    @Binds abstract fun provideExerciseService(impl: ExerciseServiceImpl): ExerciseService
}