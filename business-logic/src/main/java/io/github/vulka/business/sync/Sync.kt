package io.github.vulka.business.sync

import android.content.Context
import io.github.vulka.business.utils.getUserClient
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.types.Student
import io.github.vulka.database.Grades
import io.github.vulka.database.LuckyNumber
import io.github.vulka.database.injection.RoomModule
import io.github.vulka.impl.librus.LibrusUserClient
import java.util.Date
import java.util.UUID

suspend fun sync(
    context: Context,
    platform: Platform,
    userId: UUID,
    credentials: String,
    student: Student
) {
    val client = getUserClient(platform, credentials)

    // re-new Librus credentials
    if (platform == Platform.Librus)
        (client as LibrusUserClient).renewCredentials()

    val repository = RoomModule.providesRepository(context)

    // sync lucky number
    val newLuckyNumber = client.getLuckyNumber(student, Date())
    val luckyNumber = repository.luckyNumber.get(userId)
    if (luckyNumber != null) {
        repository.luckyNumber.update(
            luckyNumber.copy(number = newLuckyNumber)
        )
    } else {
        repository.luckyNumber.insert(
            LuckyNumber(
                id = userId,
                number = newLuckyNumber
            )
        )
    }

    // sync grades
    val grades = client.getGrades(student)
    repository.grades.deleteByCredentialsId(userId)
    for (grade in grades) {
        repository.grades.insert(
            Grades(
                grade = grade,
                credentialsId = userId
            )
        )
    }
}
