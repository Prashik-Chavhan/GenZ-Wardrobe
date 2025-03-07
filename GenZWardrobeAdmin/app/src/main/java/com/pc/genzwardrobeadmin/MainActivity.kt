package com.pc.genzwardrobeadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pc.genzwardrobeadmin.presentation.navigation.AppNavigation
import com.pc.genzwardrobeadmin.ui.theme.GenZWardrobeAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            GenZWardrobeAdminTheme {
                AppNavigation()
            }
        }
    }
}
