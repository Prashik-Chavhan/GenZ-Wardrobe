package com.pc.genzwardrobe.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pc.genzwardrobe.data.remote.PreferenceDatastore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceDatastoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
): PreferenceDatastore {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
    }

    override suspend fun saveSelectedAddressId(addressId: Int) {
        context.dataStore.edit {
            it[intPreferencesKey("selected_address_id")] = addressId
        }
    }

    override suspend fun updateSelectedAddressId(newAddressId: Int) {
        context.dataStore.edit {
            it[intPreferencesKey("selected_address_id")] = newAddressId
        }
    }

    override suspend fun getSelectedAddressId(): Int {
        return context.dataStore.data.map {
            it[intPreferencesKey("selected_address_id")] ?: 0
        }.first()
    }
}