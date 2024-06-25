import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    fun loadListsFromSharedPreferences(listName: String): MutableList<String> {
        val gson = Gson()
        val json = sharedPreferences.getString(listName, null)

        val list = mutableListOf<String>()

        json?.let {
            list.addAll(gson.fromJson(it, object : TypeToken<List<String>>() {}.type))
        }

        return list
    }

    fun saveListsToSharedPreferences(listName: String, list: MutableList<String>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()

        editor.putString(listName, gson.toJson(list))
        editor.apply()
    }
}
