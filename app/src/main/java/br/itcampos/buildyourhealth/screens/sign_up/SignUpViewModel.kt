package br.itcampos.buildyourhealth.screens.sign_up

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.itcampos.buildyourhealth.R.string as AppText
import br.itcampos.buildyourhealth.commom.ext.isValidEmail
import br.itcampos.buildyourhealth.commom.ext.isValidPassword
import br.itcampos.buildyourhealth.commom.snackbar.SnackbarManager
import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.UserStorageService
import br.itcampos.buildyourhealth.navigation.Routes.HOME_SCREEN
import br.itcampos.buildyourhealth.navigation.Routes.SIGNUP_SCREEN
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "SignUpViewModel"

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
    private val userStorageService: UserStorageService,
    //logService: LogService
) : ViewModel() {

    var uiState = mutableStateOf(SignUpUiState())
        private set

    private val name
        get() = uiState.value.name

    private val email
        get() = uiState.value.email

    private val password
        get() = uiState.value.password


    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        if (name.isBlank()) {
            SnackbarManager.showMessage(AppText.empty_name)
            return
        }
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(AppText.password_error)
            return
        }

        uiState.value = uiState.value.copy(isLoading = true)

        viewModelScope.launch() {
            try {
                val uid = accountService.createAccount(email, password)
                if (uid != null) {
                    userStorageService.saveUser(uid, name, email)
                    openAndPopUp(HOME_SCREEN, SIGNUP_SCREEN)
                } else {
                    SnackbarManager.showMessage(AppText.error_firebase_on_user_already_exist)
                }
            } catch (e: FirebaseAuthException) {
                SnackbarManager.showMessage(AppText.error_firebase_on_user_creation)
            } catch (e: Exception) {
                SnackbarManager.showMessage(AppText.unknow_error)
            }
            uiState.value = uiState.value.copy(isLoading = false)
        }
    }
}