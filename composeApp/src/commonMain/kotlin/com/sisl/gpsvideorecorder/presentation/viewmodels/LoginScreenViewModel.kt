package com.sisl.gpsvideorecorder.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sisl.gpsvideorecorder.data.remote.response.ApiResponse
import com.sisl.gpsvideorecorder.domain.models.LoginRequest
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.state.LoginScreenUiState
import com.sisl.gpsvideorecorder.presentation.state.VideoHistoryUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(private val repository: LocationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.asStateFlow()

    fun onLoginClicked(userId: String, pass: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(userId, pass)
                repository.userLogin(request).collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    errorMessage = null,
                                    isLoading = false,
                                    loginResponse = response.data
                                )
                            }
                        }

                        is ApiResponse.Error -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    errorMessage = response.message,
                                    isLoading = false,
                                    showErrorDialog = true
                                )
                            }
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    errorMessage = null,
                                    isLoading = true
                                )
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = ex.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun dismissErrorDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                showErrorDialog = false,
                errorMessage = null
            )
        }
    }


}