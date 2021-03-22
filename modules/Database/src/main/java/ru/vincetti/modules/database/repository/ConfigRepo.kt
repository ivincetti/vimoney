package ru.vincetti.modules.database.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.modules.core.models.Config
import ru.vincetti.modules.database.sqlite.ConfigDao
import ru.vincetti.modules.database.sqlite.models.ConfigModel
import javax.inject.Inject

class ConfigRepo private constructor(
    private val configDao: ConfigDao,
    private val dispatcher: CoroutineDispatcher
) {

    @Inject
    constructor(configDao: ConfigDao) : this(configDao, Dispatchers.IO)

    companion object {
        private const val CONFIG_KEY_NAME_DATE_EDIT = "date_edit"
    }

    suspend fun loadByKey(): Config? = withContext(dispatcher) {
        configDao.loadConfigByKey(CONFIG_KEY_NAME_DATE_EDIT)?.toConfig()
    }

    suspend fun add(timeStamp: String) = withContext(dispatcher) {
        configDao.insertConfig(
            ConfigModel(
                keyName = CONFIG_KEY_NAME_DATE_EDIT,
                value = timeStamp
            )
        )
    }

    suspend fun update(conf: Config) = withContext(dispatcher) {
        configDao.updateConfig(ConfigModel.from(conf))
    }
}
