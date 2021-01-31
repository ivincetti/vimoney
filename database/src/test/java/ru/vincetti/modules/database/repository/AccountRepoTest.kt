package ru.vincetti.modules.database.repository

import android.os.Build
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.database.sqlite.AppDatabase

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AccountRepoTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var transactionRepo: TransactionRepo
    private lateinit var accountRepo: AccountRepo

    @Before
    fun initDB() = testScope.runBlockingTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()

        transactionRepo = TransactionRepo(appDatabase.transactionDao())
        accountRepo = AccountRepo(
            appDatabase.accountDao(),
            transactionRepo
        )

        transactionRepo.deleteAll()
        accountRepo.deleteAll()
    }

    @Test
    fun balanceUpdate() = testScope.runBlockingTest {
        transactionRepo.deleteAll()
        accountRepo.deleteAll()

        accountRepo.add(
            Account(
                0,
                "Test",
                Account.ACCOUNT_TYPE_CASH,
                0,
                Account.DEFAULT_CURRENCY,
                "",
                "",
                Account.DEFAULT_COLOR,
                isArchive = false,
                needAllBalance = true
            )
        )

        val accounts = accountRepo.loadAll()
        accounts?.let {
            Assert.assertTrue(it.size == 1)
        } ?: Assert.fail("Wrong account number after add")
    }
}
