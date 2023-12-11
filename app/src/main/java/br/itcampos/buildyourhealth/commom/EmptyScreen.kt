package br.itcampos.buildyourhealth.commom

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.itcampos.buildyourhealth.R
import br.itcampos.buildyourhealth.R.string as AppText
import com.google.firebase.firestore.FirebaseFirestoreException
import br.itcampos.buildyourhealth.R.drawable as AppIcon

@Composable
fun EmptyScreen(error: FirebaseFirestoreException?) {

    var message by remember {
        mutableStateOf(parseErrorMessage(error = error))
    }

    var icon by remember {
        mutableStateOf(AppIcon.ic_error)
    }

    if (error == null) {
        message = stringResource(AppText.you_have_not_saved_training_so_far)
        icon = AppIcon.ic_document
    }

    var startAnimation by remember {
        mutableStateOf(false)
    }

    val alphaAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 0.3f else 0f,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
    }

    EmptyContent(alphaAnim = alphaAnimation, message = message, iconId = icon)

}

@Composable
fun EmptyContent(alphaAnim: Float, message: String, iconId: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier
                .size(120.dp)
                .alpha(alphaAnim)
        )
        Text(
            modifier = Modifier
                .padding(10.dp)
                .alpha(alphaAnim),
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
        )
    }
}


fun parseErrorMessage(error: FirebaseFirestoreException?): String {
    return when (error?.code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
            "Permissão negada"
        }

        FirebaseFirestoreException.Code.NOT_FOUND -> {
            "Nada encontrado!"
        }

        else -> {
            "Erro desconhecido"
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyScreenPreview() {
    EmptyContent(alphaAnim = 0.3f, message = "Error.", AppIcon.ic_error)
}
