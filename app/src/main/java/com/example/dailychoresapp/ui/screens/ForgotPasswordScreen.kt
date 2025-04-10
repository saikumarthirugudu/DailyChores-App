package com.example.dailychoresapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailychoresapp.viewmodel.AuthViewModel

class ForgotPasswordScreen {
    @SuppressLint("NotConstructor")
    @Composable
    fun ForgotPasswordScreen(authViewModel: AuthViewModel = viewModel()) {
        var email by remember { mutableStateOf("") }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Enter your email") })
            Button(onClick = { authViewModel.forgotPassword(email) }) {
                Text("Reset Password")
            }
        }
    }

}