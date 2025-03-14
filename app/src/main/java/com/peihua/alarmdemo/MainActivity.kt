package com.peihua.alarmdemo

import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.peihua.alarmdemo.ui.theme.AlermDemoTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("PermissionLaunchedDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlermDemoTheme {
                val viewModel: AlarmViewModel = viewModel()
                val state =
                    rememberMultiplePermissionsState(
                        listOf(
                            android.Manifest.permission.SET_ALARM,
                            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY
                        )
                    )
                if (state.allPermissionsGranted) {
                    viewModel.requestAlarm()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                    },
                    floatingActionButton = {
                        Column {
                            Button(onClick = {
                                viewModel.addAlarm()
                            }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "")
                            }
                            IconButton(onClick = {
                                state.launchMultiplePermissionRequest()
                                viewModel.requestAlarm()
                            }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "")
                            }
                        }

                    }
                ) { innerPadding ->
                    ContextView(
                        modifier = Modifier.padding(innerPadding), viewModel
                    )
                }
            }
        }
    }
}

private fun AlarmViewModel.addAlarm() {

}

@Composable
private fun MainActivity.ContextView(modifier: Modifier, viewModel: AlarmViewModel) {
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AlermDemoTheme {
        Greeting("Android")
    }
}