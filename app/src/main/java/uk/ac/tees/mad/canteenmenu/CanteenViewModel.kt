package uk.ac.tees.mad.canteenmenu

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.canteenmenu.data.model.MenuItem
import javax.inject.Inject

@HiltViewModel
class CanteenViewModel @Inject constructor(
    private val authentication: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val database: DatabaseReference
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    private val _dailySpecial = MutableStateFlow<MenuItem?>(null)
    val dailySpecial: StateFlow<MenuItem?> = _dailySpecial

    init {
        if (authentication.currentUser != null) {
            _loggedIn.value = true
            ensureDailySpecial()
            fetchMenu()
        }
    }

    fun logIn(context: Context, email: String, password: String) {
        _loading.value = true
        authentication.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loading.value = false
                _loggedIn.value = true
                Toast.makeText(context, "Logged in successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                _loading.value = false
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun signUp(context: Context, name: String, email: String, password: String) {
        _loading.value = true
        authentication.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { uid ->
                firebaseFirestore.collection("user").document(uid.user!!.uid)
                    .set(mapOf("name" to name, "email" to email))
                    .addOnSuccessListener {
                        _loading.value = false
                        _loggedIn.value = true
                        Toast.makeText(context, "User created successfully", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnFailureListener {
                        _loading.value = false
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener {
                _loading.value = false
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    /** ✅ Check if a special already exists today; if not, pick one */
    private fun ensureDailySpecial() {
        val menuRef = database.child("menuItems")

        menuRef.get().addOnSuccessListener { snapshot ->
            var specialItem: MenuItem? = null
            val items = snapshot.children.toList()

            for (child in items) {
                val item = child.getValue(MenuItem::class.java)
                if (item?.isSpecial == true) {
                    specialItem = item
                    break
                }
            }

            if (specialItem != null) {
                _dailySpecial.value = specialItem
            } else if (items.isNotEmpty()) {
                // Pick new special since none exists
                val randomItem = items.random()
                menuRef.child(randomItem.key!!).child("isSpecial").setValue(true)
                    .addOnSuccessListener {
                        val chosen = randomItem.getValue(MenuItem::class.java)
                        _dailySpecial.value = chosen?.copy(isSpecial = true)
                        Log.d("FoodOfTheDay", "New daily special set: ${chosen?.name}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FoodOfTheDay", "Error setting special: ${e.message}")
                    }
            }
        }
    }

    /** 🔄 Fetch menu list into StateFlow for UI */
    fun fetchMenu() {
        val menuRef = database.child("menuItems")
        menuRef.get().addOnSuccessListener { snapshot ->
            val list = snapshot.children.mapNotNull { it.getValue(MenuItem::class.java) }
            _menuItems.value = list
        }.addOnFailureListener {
            _menuItems.value = emptyList()
        }
    }
}
