//package id.melur.binar.mocktest
//
//import android.content.Context
//
//class DataStoreManager(private val context: Context)  {
//
//    suspend fun saveDataLoged(id: Int, username: String, email: String, password: String) {
//        context.dataStore.edit { pref ->
//            pref[KEY_ID] = id
//            pref[KEY_USERNAME] = username
//            pref[KEY_EMAIL] = email
//            pref[KEY_PASSWORD] = password
//            pref[KEY_LOGIN] = true
//        }
//    }
//
//    companion object {
//        private const val DATA_STORE_NAME = "loged_preference"
//        private val KEY_ID = intPreferencesKey("key_id")
//        private val KEY_USERNAME = stringPreferencesKey("key_username")
//        private val KEY_EMAIL = stringPreferencesKey("key_email")
//        private val KEY_PASSWORD = stringPreferencesKey("key_password")
//        private val KEY_LOGIN = booleanPreferencesKey("key_login")
//        private val Context.dataStore by preferencesDataStore(name = DATA_STORE_NAME)
//    }
//}