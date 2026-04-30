package com.obrit.obrit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.obrit.android.core.designsyetem.theme.OBRitTheme
import com.obrit.obrit.navigation.OBRitNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            OBRitTheme {
                OBRitNavigation()
            }
        }
    }
}
