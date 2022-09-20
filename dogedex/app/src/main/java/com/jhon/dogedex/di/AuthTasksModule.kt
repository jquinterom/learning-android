package com.jhon.dogedex.di

import com.jhon.dogedex.auth.AuthRepository
import com.jhon.dogedex.interfaces.AuthTasks
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthTasksModule {

    @Binds
    abstract fun bindDogTasks(
        dogRepository: AuthRepository
    ): AuthTasks
}