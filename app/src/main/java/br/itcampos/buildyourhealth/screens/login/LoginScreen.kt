package br.itcampos.buildyourhealth.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.itcampos.buildyourhealth.commom.BasicButton
import br.itcampos.buildyourhealth.commom.BasicDivider
import br.itcampos.buildyourhealth.commom.BasicTextField
import br.itcampos.buildyourhealth.commom.ClickableLoginText
import br.itcampos.buildyourhealth.commom.HeadingText
import br.itcampos.buildyourhealth.commom.NormalText
import br.itcampos.buildyourhealth.commom.PasswordTextField
import br.itcampos.buildyourhealth.commom.UnderLinedText
import br.itcampos.buildyourhealth.ui.theme.BuildYourHealthTheme
import br.itcampos.buildyourhealth.R.string as AppText

@Composable
fun LoginScreen(
    openAndPopUp: (String, String) -> Unit,
    onSignUpTo: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = { viewModel.onLoginClick(openAndPopUp) },
        onForgotPasswordClick = viewModel::onForgotPasswordClick,
        onSignUpTo = onSignUpTo
    )

}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpTo: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp)
            .background(Color.White)
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            NormalText(value = stringResource(AppText.login))

            HeadingText(value = stringResource(AppText.welcome_back))

            Spacer(modifier = modifier.height(20.dp))

            BasicTextField(
                uiState.email,
                onEmailChange,
                labelValue = stringResource(AppText.email),
                icon = Icons.Outlined.Email
            )

            PasswordTextField(
                uiState.password,
                onPasswordChange,
                labelValue = stringResource(AppText.password),
                icon = Icons.Outlined.Password
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = modifier.height(20.dp))

            UnderLinedText(onForgotPasswordClick ,value = stringResource(AppText.forgot_password))

            Spacer(modifier = modifier.height(20.dp))

            BasicButton(value = stringResource(AppText.log_in)) {
                onLoginClick()
            }

            Spacer(modifier = modifier.height(20.dp))

            BasicDivider()

            Spacer(modifier = modifier.height(20.dp))

            ClickableLoginText(tryingToLogin = false, onTextSelected = { onSignUpTo() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val uiState = LoginUiState(
        email = "teste@teste.com"
    )
    BuildYourHealthTheme {
        LoginScreenContent(
            uiState = uiState,
            onEmailChange = { },
            onPasswordChange = { },
            onLoginClick = { /*TODO*/ },
            onForgotPasswordClick = { /*TODO*/ }) {
            
        }
    }
}