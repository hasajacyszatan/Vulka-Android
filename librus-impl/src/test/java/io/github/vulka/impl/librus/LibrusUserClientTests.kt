package io.github.vulka.impl.librus

import kotlinx.coroutines.test.runTest
import org.junit.Test

class LibrusUserClientTests {
    private var userClient: LibrusUserClient? = null

    @Test
    fun testGetStudents() = runTest {
        val client = getUserClient()

        val students = client.getStudents()

        assert(students.size == 1)
        assert(students[0].isParent)
    }

    @Test
    fun testGetLuckyNumber() = runTest {
        val client = getUserClient()

        val students = client.getStudents()
        val luckyNumber = client.getLuckyNumber(students[0])

        assert(luckyNumber != 0)
    }

    @Test
    fun testGetAccountInfo() = runTest {
        val client = getUserClient()

        val accountInfo = client.getStudents()[0]

        assert(accountInfo.fullName != "")
        assert(accountInfo.classId != "")
//        assert(accountInfo.index != 0)
//        assert(accountInfo.educator != "")
    }

    private suspend fun getUserClient(): LibrusUserClient {
        if (userClient != null) {
            return userClient as LibrusUserClient
        }

        val credentials = LibrusLoginClient().login(LOGIN_DATA)

        val client = LibrusUserClient(credentials as LibrusLoginCredentials)
        client.initClient(credentials.cookies)

        userClient = client

        return client
    }
}
