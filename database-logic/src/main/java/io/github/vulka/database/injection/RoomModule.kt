package io.github.vulka.database.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.vulka.database.CredentialsDao
import io.github.vulka.database.GradesDao
import io.github.vulka.database.LuckyNumberDao
import io.github.vulka.database.Repository
import io.github.vulka.database.SemestersDao
import io.github.vulka.database.Timetable
import io.github.vulka.database.TimetableDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Singleton
    @Provides
    fun providesRepository(
        @ApplicationContext context: Context
    ): Repository {
        return Repository(context)
    }

    @Singleton
    @Provides
    fun provideCredentialRepository(repository: Repository): CredentialsDao {
        return repository.credentials
    }

    @Singleton
    @Provides
    fun provideLuckyNumberRepository(repository: Repository): LuckyNumberDao {
        return repository.luckyNumber
    }

    @Singleton
    @Provides
    fun provideGradesRepository(repository: Repository): GradesDao {
        return repository.grades
    }

    @Singleton
    @Provides
    fun provideTimetableRepository(repository: Repository): TimetableDao {
        return repository.timetable
    }

    @Singleton
    @Provides
    fun provideSemestersRepository(repository: Repository): SemestersDao {
        return repository.semesters
    }
}
