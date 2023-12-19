package br.itcampos.buildyourhealth.commom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import br.itcampos.buildyourhealth.screens.training.CardEditors
import br.itcampos.buildyourhealth.ui.state.TrainingScreenUiState
import br.itcampos.buildyourhealth.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTrainingComposable(
    setTrainingName: (String) -> Unit,
    setTrainingDescription: (String) -> Unit,
    setTrainingDate: (String) -> Unit,
    addTraining: () -> Unit,
    closeDialog: () -> Unit,
    uiState: TrainingScreenUiState
) {

    Dialog(onDismissRequest = { closeDialog() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Novo Treino",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                modifier = Modifier.clickable { closeDialog() },
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = "Cancelar"
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = uiState.currentTextFieldName,
                            onValueChange = { name ->
                                setTrainingName(name)
                            },
                            label = { Text("Treino") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                unfocusedLabelColor = Color.Black
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.currentTextFieldDescription,
                            onValueChange = { description ->
                                setTrainingDescription(description)
                            },
                            label = { Text("Descrição") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                unfocusedLabelColor = Color.Black
                            ),
                            maxLines = 3
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CardEditors(setTrainingDateChange = setTrainingDate)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    addTraining()
                                    setTrainingName("")
                                    setTrainingDescription("")
                                    closeDialog()
                                },
                                modifier = Modifier.padding(horizontal = 12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(text = "Salvar")
                            }
                        }
                    }
                }
            }
        }
    }
}