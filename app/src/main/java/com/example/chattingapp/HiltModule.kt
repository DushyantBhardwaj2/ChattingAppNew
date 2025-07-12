package com.example.chattingapp

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Provides
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.chattingapp.diagnostic.FirebaseDataDiagnostic

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseDataDiagnostic(
        firestore: FirebaseFirestore
    ): FirebaseDataDiagnostic = FirebaseDataDiagnostic(firestore)
}
