package com.sisl.gpsvideorecorder.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sisl.gpsvideorecorder.CheckBoxBorderColor
import com.sisl.gpsvideorecorder.PrimaryColor
import com.sisl.gpsvideorecorder.presentation.components.EditTextField
import com.sisl.gpsvideorecorder.presentation.components.RedButtonCTA
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.ic_next
import gpsvideorecorder.composeapp.generated.resources.ic_login_bg
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}
) {
    val userName = remember { mutableStateOf("") }
    val userPassword = remember { mutableStateOf("") }
    val userRememberChecked = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(Res.drawable.ic_login_bg),
                    modifier = Modifier.size(200.dp),
                    contentDescription = "login",
                    alignment = Alignment.Center
                )
                Text(
                    text = "Welcome Back!",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 25.dp)
                )
                Text(
                    text = "sing in to access your account!",
                    fontSize = 15.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 70.dp)
                )
            }

            EditTextField(
                value = userName.value,
                onValueChange = { userName.value = it },
                hint = "User Name",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                drawableTrailingIcon = Icons.Default.Email,
            )
            Spacer(modifier = Modifier.height(12.dp))
            EditTextField(
                value = userPassword.value,
                onValueChange = { userPassword.value = it },
                hint = "User Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                drawableTrailingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = userRememberChecked.value,
                    onCheckedChange = { userRememberChecked.value = it },
                    modifier = Modifier.padding(start = 6.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryColor, uncheckedColor = CheckBoxBorderColor
                    )
                )
                Text(
                    text = "Remember Me",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }

        }
        RedButtonCTA(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 50.dp)
                .height(48.dp)
                .align(Alignment.BottomCenter),
            onButtonClicked = {
                // Add basic validation
                if (userName.value.isNotBlank() && userPassword.value.isNotBlank()) {
                    onLoginSuccess()
                }
            },
            buttonText = "Login",
            drawableIcon = Res.drawable.ic_next
        )
    }
}



