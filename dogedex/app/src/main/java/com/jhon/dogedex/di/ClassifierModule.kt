package com.jhon.dogedex.di

import com.jhon.dogedex.interfaces.ClassifierTasks
import com.jhon.dogedex.machinelearning.ClassifierRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ClassifierModule {
    @Binds
    abstract fun bindClassifierTasks(
        classifierRepository: ClassifierRepository
    ): ClassifierTasks
}