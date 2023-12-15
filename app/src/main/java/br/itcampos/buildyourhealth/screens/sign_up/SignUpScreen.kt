package br.itcampos.buildyourhealth.screens.sign_up

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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import br.itcampos.buildyourhealth.commom.BasicButton
import br.itcampos.buildyourhealth.commom.BasicDivider
import br.itcampos.buildyourhealth.commom.BasicTextField
import br.itcampos.buildyourhealth.commom.CircularProgressComposable
import br.itcampos.buildyourhealth.commom.ClickableLoginText
import br.itcampos.buildyourhealth.commom.HeadingText
import br.itcampos.buildyourhealth.commom.NormalText
import br.itcampos.buildyourhealth.commom.PasswordTextField
import br.itcampos.buildyourhealth.ui.theme.BuildYourHealthTheme
import br.itcampos.buildyourhealth.R.string as AppText

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    onLoginTo: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    SignUpScreenContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignUpClick = { viewModel.onSignUpClick(openAndPopUp) },
        onLoginTo = onLoginTo
    )
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onLoginTo: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp)
            .background(Color.White)
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            NormalText(value = stringResource(AppText.hello))

            HeadingText(value = stringResource(AppText.create_your_account))

            Spacer(modifier = modifier.height(20.dp))

            BasicTextField(
                uiState.name,
                onNameChange,
                labelValue = stringResource(AppText.first_name),
                icon = Icons.Outlined.Person
            )
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
                CircularProgressComposable()
            }

            Spacer(modifier = modifier.height(20.dp))

            BasicButton(value = stringResource(AppText.register)) {
                onSignUpClick()
            }

            Spacer(modifier = modifier.height(20.dp))

            BasicDivider()

            Spacer(modifier = modifier.height(20.dp))

            ClickableLoginText(tryingToLogin = true, onTextSelected = { onLoginTo() })
        }
    }

}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val uiState = SignUpUiState(
        email = "teste@teste.com"
    )
    BuildYourHealthTheme {
        SignUpScreenContent(
            uiState = uiState,
            onNameChange = { },
            onEmailChange = { },
            onPasswordChange = { },
            onSignUpClick = { }
        ) {
        }
    }
}