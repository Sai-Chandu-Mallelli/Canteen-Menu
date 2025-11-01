package uk.ac.tees.mad.canteenmenu

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CanteenViewModel @Inject constructor(
    private val authentication: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    val loading = mutableStateOf(false)
    val loggedIn = mutableStateOf(false)
    init {
        if (authentication.currentUser != null) {
            loggedIn.value = true
        }
    }

    fun logIn(context: Context, email: String, password: String) {
        loading.value = true
        authentication.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loading.value = false
                loggedIn.value = true
                Toast.makeText(context, "Logged in successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                loading.value = false
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun signUp(context: Context, name: String, email: String, password: String) {
        loading.value = true
        authentication.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { uid ->
                firebaseFirestore.collection("user").document(uid.user!!.uid)
                    .set(mapOf("name" to name, "email" to email)).addOnSuccessListener {
                    loading.value = false
                    loggedIn.value = true
                        Toast.makeText(context, "User created successfully", Toast.LENGTH_SHORT)
                            .show()
                }.addOnFailureListener { it->
                    loading.value = false
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                loading.value = false
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}