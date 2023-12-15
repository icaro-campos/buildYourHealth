package br.itcampos.buildyourhealth.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import br.itcampos.buildyourhealth.navigation.AppNavigationGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuildYourHealthActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigationGraph()
        }
    }
}