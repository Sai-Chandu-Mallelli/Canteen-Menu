package uk.ac.tees.mad.canteenmenu.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PanoramaFishEye
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.canteenmenu.CanteenViewModel
import uk.ac.tees.mad.canteenmenu.R

@Composable
fun Authentication(viewModel: CanteenViewModel, navController: NavHostController) {

    val loading = viewModel.loading.value
    val context = LocalContext.current
    val loggedIn = viewModel.loggedIn.value

    LaunchedEffect(loggedIn) {
        if (loggedIn) {
            navController.navigate(Routes.HOME)
        }
    }
    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val loginOrSignup = remember { mutableStateOf(1) }
        when (loginOrSignup.value) {
            1 -> Login(context = context, onChange = { loginOrSignup.value = 2 }, onLogIn = { email, password ->
                viewModel.logIn(email = email, password = password, context = context)
                    }
                )
            2 -> Signup(context = context, onChange = { loginOrSignup.value = 1 }, onSignUp = { name, email, password ->
                    viewModel.signUp(name = name, email = email, password = password, context = context)
                    }
                )
        }
    }
}


@Composable
fun Login(context: Context, onChange: () -> Unit, onLogIn: (email: String, password: String) -> Unit){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val isPasswordVisible = remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.padding(4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painterResource(R.drawable.studying), contentDescription = null)
            Image(painterResource(R.drawable.raise_hand), contentDescription = null)
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Log in to Canteen Menu", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(10.dp))
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier.padding(5.dp),
                singleLine = true,
                shape = RoundedCornerShape(50.dp)
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier.padding(5.dp),
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                trailingIcon = {
                    Icon(if (isPasswordVisible.value) Icons.Rounded.PanoramaFishEye else Icons.Rounded.RemoveRedEye,contentDescription = null, modifier = Modifier.padding(end = 10.dp).clickable {
                        isPasswordVisible.value = !isPasswordVisible.value
                        }
                    )
                },
                visualTransformation =  if (!isPasswordVisible.value) {PasswordVisualTransformation()} else {androidx.compose.ui.text.input.VisualTransformation.None}
            )
            Button(onClick = {
                if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                    onLogIn(email.value, password.value)
                }else{
                    Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                }
            },
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 60.dp).fillMaxWidth()) {
                Text("Login", fontSize = 18.sp)
            }
            Row(modifier = Modifier.padding(vertical = 10.dp)) {
                Text("Don't have an account?", modifier = Modifier.padding(vertical = 10.dp))
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Text("Sign up", modifier = Modifier.padding(vertical = 10.dp).clickable {
                    onChange()
                }, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Signup(context: Context, onChange: () -> Unit, onSignUp: (name: String, email: String, password: String) -> Unit){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        val name = remember { mutableStateOf("") }
        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val isPasswordVisible = remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.padding(4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painterResource(R.drawable.studying), contentDescription = null)
            Image(painterResource(R.drawable.raise_hand), contentDescription = null)
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sign up to Canteen Menu", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(10.dp))
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Name") },
                modifier = Modifier.padding(5.dp),
                singleLine = true,
                shape = RoundedCornerShape(50.dp)
            )
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier.padding(5.dp),
                singleLine = true,
                shape = RoundedCornerShape(50.dp)
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier.padding(5.dp),
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                trailingIcon = {
                    Icon(if (isPasswordVisible.value) Icons.Rounded.PanoramaFishEye else Icons.Rounded.RemoveRedEye,contentDescription = null, modifier = Modifier.padding(end = 10.dp).clickable {
                        isPasswordVisible.value = !isPasswordVisible.value
                        }
                    )
                },
                visualTransformation =  if (!isPasswordVisible.value) {PasswordVisualTransformation()} else {androidx.compose.ui.text.input.VisualTransformation.None}
            )
            Button(onClick = {
                if (name.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty()) {
                    onSignUp(name.value, email.value, password.value)
                }else{
                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
                }
            },
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 60.dp).fillMaxWidth()) {
                Text("Sign up", fontSize = 18.sp)
            }
            Row(modifier = Modifier.padding(vertical = 10.dp)) {
                Text("Already have an account?", modifier = Modifier.padding(vertical = 10.dp))
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Text("Log in", modifier = Modifier.padding(vertical = 10.dp).clickable {
                    onChange()
                }, fontWeight = FontWeight.Bold)
            }
        }
    }
}