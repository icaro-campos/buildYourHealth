package br.itcampos.buildyourhealth.screens.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import br.itcampos.buildyourhealth.R
import br.itcampos.buildyourhealth.R.string as AppText
import br.itcampos.buildyourhealth.Routes
import br.itcampos.buildyourhealth.Routes.HOME_SCREEN
import br.itcampos.buildyourhealth.Routes.LOGIN_SCREEN
import br.itcampos.buildyourhealth.commom.ext.isValidEmail
import br.itcampos.buildyourhealth.commom.ext.isValidPassword
import br.itcampos.buildyourhealth.commom.snackbar.SnackbarManager
import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.LogService
import br.itcampos.buildyourhealth.screens.BuildYourHealthViewModel
import br.itcampos.buildyourhealth.screens.sign_up.TAG
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : BuildYourHealthViewModel(logService) {

    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email

    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onLoginClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(AppText.password_error)
            return
        }

        uiState.value = uiState.value.copy(isLoading = true)

        launchCatching {
            try {
                accountService.authenticate(email, password)
                openAndPopUp(HOME_SCREEN, LOGIN_SCREEN)
            } catch (e: FirebaseAuthException) {
                SnackbarManager.showMessage(AppText.error_firebase_on_user_creation)
                Log.d(TAG, "onSignUpClick: ${e.message} -> ${e.localizedMessage}")
            } catch (e: Exception) {
                SnackbarManager.showMessage(AppText.unknow_error)
                Log.d(TAG, "onSignUpClick: ${e.message} -> ${e.localizedMessage}")
            }
            uiState.value = uiState.value.copy(isLoading = false)
        }
    }

    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            return
        }

        launchCatching {
            accountService.sendRecoveryEmail(email)
            SnackbarManager.showMessage(AppText.recovery_email_sent)
        }
    }
}