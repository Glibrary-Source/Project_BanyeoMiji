package com.twproject.banyeomiji.view

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserSelectManager(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val USER_SELECT_KEY = intPreferencesKey("LOCATION_SELECT")
    }

    suspend fun selectUser(
        position: Int
    ) {
        dataStore.edit {
            it[USER_SELECT_KEY] = position
        }
    }

    val userSelectFlow: Flow<Int?> = dataStore.data.map {
        it[USER_SELECT_KEY]
    }
}