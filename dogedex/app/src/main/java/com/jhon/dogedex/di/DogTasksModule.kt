package com.jhon.dogedex.di

import com.jhon.dogedex.doglist.DogRepository
import com.jhon.dogedex.interfaces.DogTasks
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DogTasksModule {

    @Binds
    abstract fun bindDogTasks(
        dogRepository: DogRepository
    ): DogTasks
}