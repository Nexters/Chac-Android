package com.chac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.chac.core.designsystem.ui.theme.ChacTheme
import com.chac.core.permission.MediaWithLocationPermissionUtil.launchMediaWithLocationPermission
import com.chac.core.permission.compose.PermissionDeniedDialog
import com.chac.core.permission.compose.moveToPermissionSetting
import com.chac.core.permission.compose.rememberRegisterMediaWithLocationPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChacTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                    )
                    PermissionSample()
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChacTheme {
        Greeting("Android")
    }
}

@Composable
fun PermissionSample() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        var showPermissionDeniedDialog by remember { mutableStateOf(false) }

        val permission =
            rememberRegisterMediaWithLocationPermission(
                onGranted = {},
                onPermanentlyDenied = { showPermissionDeniedDialog = true },
                onDenied = { showPermissionDeniedDialog = true },
            )

        Button(
            onClick = {
                permission.launchMediaWithLocationPermission()
            },
        ) {
            Text("권한요청")
        }

        if (showPermissionDeniedDialog) {
            val context = LocalContext.current
            PermissionDeniedDialog(
                title = "권한 필요",
                message = "메시지",
                onDismissRequest = {
                    showPermissionDeniedDialog = false
                },
                onPositiveClick = {
                    showPermissionDeniedDialog = false
                    moveToPermissionSetting(context)
                },
            )
        }
    }
}
