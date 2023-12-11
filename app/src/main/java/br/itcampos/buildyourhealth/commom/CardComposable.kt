package br.itcampos.buildyourhealth.commom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun RegularCardEditor(
    title: String,
    icon: ImageVector,
    content: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CardEditor(title, icon, content, onClick, MaterialTheme.colors.onSurface, modifier)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardEditor(
    title: String,
    icon: ImageVector,
    content: String,
    onClick: () -> Unit,
    highlightColor: Color,
    modifier: Modifier
) {
    Card(
        backgroundColor = MaterialTheme.colors.onPrimary,
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = highlightColor
                )
            }
            if (content.isNotBlank()) {
                Text(text = content, modifier = Modifier.padding(16.dp, 0.dp))
            }
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = highlightColor
            )
        }
    }
}