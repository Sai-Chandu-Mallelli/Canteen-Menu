package uk.ac.tees.mad.canteenmenu

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.canteenmenu.data.dao.MenuItemDao
import uk.ac.tees.mad.canteenmenu.data.model.MenuItem
import uk.ac.tees.mad.canteenmenu.data.model.UserData
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CanteenViewModel @Inject constructor(
    private val authentication: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val database: DatabaseReference,
    private val menuItemDao: MenuItemDao,
    private val cloudinary: Cloudinary
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    private val _dailySpecial = MutableStateFlow<MenuItem?>(null)
    val dailySpecial: StateFlow<MenuItem?> = _dailySpecial

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    private val _orders = MutableStateFlow<List<MenuItem>>(emptyList())
    val orders: StateFlow<List<MenuItem>> = _orders


    init {
        if (authentication.currentUser != null) {
            _loggedIn.value = true
            ensureDailySpecial()
            fetchMenu()
            getUserData()
            fetchFromDatabase()
        }
    }

    fun onAddAmount(context: Context,amount : Double){
        _loading.value = true
        val userId = authentication.currentUser?.uid
        val balance = _userData.value?.balance ?: 0.0
        firebaseFirestore.collection("user").document(userId!!).update("balance", balance + amount)
            .addOnSuccessListener {
                _loading.value = false
                getUserData()
                Toast.makeText(context, "Amount added successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                _loading.value = false
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun getUserData() {
        val userId = authentication.currentUser?.uid
        firebaseFirestore.collection("user").document(userId!!).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userData = document.toObject(UserData::class.java)
                    _userData.value = userData
                    Log.d("UserData", "User data: $userData")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserData", "Error getting user data: ${exception.message}")
            }
    }

    fun logIn(context: Context, email: String, password: String) {
        _loading.value = true
        authentication.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loading.value = false
                _loggedIn.value = true
                ensureDailySpecial()
                fetchMenu()
                getUserData()
                fetchFromDatabase()
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
                    .set(UserData("", name, email, 2000.00))
                    .addOnSuccessListener {
                        _loading.value = false
                        _loggedIn.value = true
                        ensureDailySpecial()
                        fetchMenu()
                        getUserData()
                        fetchFromDatabase()
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

    fun fetchMenu() {
        val menuRef = database.child("menuItems")
        menuRef.get().addOnSuccessListener { snapshot ->
            val list = snapshot.children.mapNotNull { it.getValue(MenuItem::class.java) }
            _menuItems.value = list
        }.addOnFailureListener {
            _menuItems.value = emptyList()
        }
    }



    fun storeInDatabase(context: Context,menuItem: MenuItem){
        viewModelScope.launch {
            try {
                menuItemDao.insertMenuItems(menuItem)
                fetchFromDatabase()
                Toast.makeText(context, "Ordered Successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error Ordering", Toast.LENGTH_SHORT).show()
                Log.e("CanteenViewModel", "Error Puttin into database", e)
            }
        }
    }

    fun fetchFromDatabase(){
        viewModelScope.launch {
            try {
                menuItemDao.getAllMenuItems().collect {
                    _orders.value = it
                    Log.d("CanteenViewModel", "Data inserted successfully: $it")
                }
            } catch (e: Exception) {
                Log.e("CanteenViewModel", "Error fetching data from Room", e)
            }
        }
    }

    fun makeOrder(context: Context,menuItem: MenuItem) {
        val price = menuItem.price
        val balance = _userData.value?.balance
        val finalBalance = balance?.minus(price)
        if (finalBalance != null) {
            if (finalBalance >= 0) {
                storeInDatabase(context, menuItem)
                firebaseFirestore.collection("user").document(authentication.currentUser!!.uid)
                    .update("balance", finalBalance).addOnSuccessListener {
                        getUserData()
                    }.addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Insufficient Balance", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun uploadProfileImage(context: Context, imageUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                when (imageUri.scheme) {
                    "file" -> {
                        val file = File(imageUri.path!!)
                        val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
                        result["url"] as? String
                    }
                    "content" -> {
                        context.contentResolver.openInputStream(imageUri).use { inputStream ->
                            if (inputStream != null) {
                                val result = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                                result["secure_url"] as? String
                            } else null
                        }
                    }
                    else -> {
                        Log.e("ProfileUpload", "Unsupported Uri scheme: ${imageUri.scheme}")
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileUpload", "Upload failed: ${e.message}")
                null
            }
        }
    }

    fun updateUserData(context: Context, imageUri: Uri?, name: String, email: String) {
        viewModelScope.launch {
            var uploadedUrl: String? = null

            if (imageUri != null) {
                uploadedUrl = uploadProfileImage(context, imageUri)
            }

            val userUpdate = mutableMapOf<String, Any>(
                "name" to name,
                "email" to email
            )

            uploadedUrl?.let { userUpdate["profilePhoto"] = it }

            firebaseFirestore.collection("user")
                .document(authentication.currentUser!!.uid)
                .update(userUpdate)
                .addOnSuccessListener {
                    getUserData()
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun logOut() {
        authentication.signOut()
        _loggedIn.value = false
        _userData.value = null
    }
}
