package ru.vincetti.vimoney.data.repository

import ru.vincetti.vimoney.data.models.ConfigModel
import ru.vincetti.vimoney.data.sqlite.ConfigDao
import javax.inject.Inject

class ConfigRepo @Inject constructor(
    private val configDao: ConfigDao
) {

    companion object {
        private const val CONFIG_KEY_NAME_DATE_EDIT = "date_edit"
    }

    suspend fun loadByKey(): ConfigModel? {
        return configDao.loadConfigByKey(CONFIG_KEY_NAME_DATE_EDIT)
    }

    suspend fun add(timeStamp: String) {
        configDao.insertConfig(
            ConfigModel(
                keyName = CONFIG_KEY_NAME_DATE_EDIT,
                value = timeStamp
            )
        )
    }

    suspend fun update(conf: ConfigModel) {
        configDao.updateConfig(conf)
    }
}
