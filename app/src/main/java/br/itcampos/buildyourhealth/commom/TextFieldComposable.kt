@file:OptIn(ExperimentalMaterial3Api::class)

package br.itcampos.buildyourhealth.commom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import br.itcampos.buildyourhealth.ui.theme.BgColor
import br.itcampos.buildyourhealth.ui.theme.Primary
import br.itcampos.buildyourhealth.R.string as AppText

@Composable
fun BasicTextField(
    value: String,
    onNewValue: (String) -> Unit,
    labelValue: String,
    icon: ImageVector
) {

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(4.dp)),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            cursorColor = Primary,
            containerColor = BgColor
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        maxLines = 1,
        value = value,
        onValueChange = { onNewValue(it)},
        leadingIcon = { Icon(imageVector = icon, contentDescription = "Icon") },

    )
}

@Composable
fun GeneralBasicField(
    value: String,
    onNewValue: (String) -> Unit,
    labelValue: String,
) {
    OutlinedTextField(
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
            .clip(shape = RoundedCornerShape(4.dp)),
        value = value,
        onValueChange = { onNewValue(it) },
        label = { Text(text = labelValue) }
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onNewValue: (String) -> Unit,
    labelValue: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val localFocusManager = LocalFocusManager.current
    val passwordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(4.dp)),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            cursorColor = Primary,
            containerColor = BgColor
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
        keyboardActions = KeyboardActions {
            localFocusManager.clearFocus()
        },
        singleLine = true,
        maxLines = 1,
        value = value,
        onValueChange = { onNewValue(it) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = "Icon") },
        trailingIcon = {
            val iconPassword = if (passwordVisible.value) {
                Icons.Outlined.Visibility
            } else {
                Icons.Outlined.VisibilityOff
            }

            val descriptionContent = if (passwordVisible.value) {
                stringResource(AppText.hide_password)
            } else {
                stringResource(AppText.show_password)
            }

            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = iconPassword, contentDescription = descriptionContent)
            }
        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else
            PasswordVisualTransformation()
    )
}