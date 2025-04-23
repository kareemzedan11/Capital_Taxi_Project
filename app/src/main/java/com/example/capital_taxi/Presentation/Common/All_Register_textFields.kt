package com.example.capital_taxi.Presentation.Common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R

@Composable
fun All_Register_textFields(
    name: MutableState<String>,
    username: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>,
    phone: MutableState<String>,
    carType: MutableState<String>? = null,
    carNumber: MutableState<String>? = null,
    carColor: MutableState<String>? = null,
    carModel: MutableState<String>? = null
) {
    RegisterTextField(
        value = name.value,
        onValueChange = { name.value = it },
        label = "Full Name",
        keyboardType = KeyboardType.Text
    )

    Spacer(modifier = Modifier.height(16.dp))

    RegisterTextField(
        value = username.value,
        onValueChange = { username.value = it },
        label = "username",
        keyboardType = KeyboardType.Text
    )

    Spacer(modifier = Modifier.height(16.dp))

    RegisterTextField(
        value = email.value,
        onValueChange = { email.value = it },
        label = stringResource(id = R.string.email),
        keyboardType = KeyboardType.Email
    )

    Spacer(modifier = Modifier.height(16.dp))

    PasswordField(
        value = password.value,
        onValueChange = { password.value = it },
        label = stringResource(id = R.string.password),
        isVisible = false,
        onVisibilityChange = { }
    )

    Spacer(modifier = Modifier.height(16.dp))

    PasswordField(
        value = confirmPassword.value,
        onValueChange = { confirmPassword.value = it },
        label = stringResource(id = R.string.confirm_password),
        isVisible = false,
        onVisibilityChange = { }
    )

    Spacer(modifier = Modifier.height(16.dp))

    RegisterTextField(
        value = phone.value,
        onValueChange = { phone.value = it },
        label = stringResource(id = R.string.phone),
        keyboardType = KeyboardType.Phone
    )

    // عرض حقول السيارة فقط إذا كانت غير null
    carType?.let { state->
        Spacer(modifier = Modifier.height(16.dp))
        RegisterTextField(
            value = state.value,
            onValueChange = { newValue -> state.value = newValue },
            label = "Car Type",
            keyboardType = KeyboardType.Text
        )
    }

    carNumber?.let { state->
        Spacer(modifier = Modifier.height(16.dp))
        RegisterTextField(
            value = state.value,
            onValueChange = { newValue -> state.value = newValue },
            label = "Car Number",
            keyboardType = KeyboardType.Text
        )
    }

    carColor?.let {state->
        Spacer(modifier = Modifier.height(16.dp))
        RegisterTextField(
            value = state.value,
            onValueChange = { newValue -> state.value = newValue },
            label = "Car Color",
            keyboardType = KeyboardType.Text
        )
    }

    carModel?.let {state->
        Spacer(modifier = Modifier.height(16.dp))
        RegisterTextField(
            value = state.value,
            onValueChange = { newValue -> state.value = newValue },
            label = "Car Model",
            keyboardType = KeyboardType.Text
        )
    }
}