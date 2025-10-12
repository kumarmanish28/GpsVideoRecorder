package com.sisl.gpsvideorecorder.presentation.state

import com.sisl.gpsvideorecorder.domain.models.LoginResponse

data class LoginScreenUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginResponse: LoginResponse? = null,
    val showErrorDialog: Boolean = false
)