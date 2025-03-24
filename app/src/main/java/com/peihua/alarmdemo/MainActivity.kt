package com.peihua.alarmdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.peihua.alarmdemo.ui.theme.AlermDemoTheme
import com.peihua.alarmdemo.utils.ResultData
import com.peihua.alarmdemo.utils.dLog
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    @SuppressLint("PermissionLaunchedDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dLog { "alarmClockInfo>>>>>onCreate" }
        enableEdgeToEdge()
        setContent {
            AlermDemoTheme {
                val viewModel: AlarmViewModel = viewModel()
                val showDialog = remember { mutableStateOf(false) }
                val setAlarm = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.SCHEDULE_EXACT_ALARM,
                        Manifest.permission.SET_ALARM,
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                    )
                )
                val state =
                    rememberMultiplePermissionsState(
                        listOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.SCHEDULE_EXACT_ALARM,
                            Manifest.permission.SET_ALARM,
                            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                            "com.android.alarm.permission.READ_ALARMS",
                            Manifest.permission.READ_CALENDAR,
                            Manifest.permission.WRITE_CALENDAR
                        )
                    )
                if (state.allPermissionsGranted) {
                    viewModel.requestEvents()
                }
                if (setAlarm.allPermissionsGranted) {
                    viewModel.requestAlarm()
                    viewModel.addAlarm(this@MainActivity)
                    dLog { "alarmClockInfo>>>>>add alarm start" }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                    },
                    floatingActionButton = {
                        Column(modifier = Modifier
                            .padding(16.dp)
                            .background(Color.White)) {
                            IconButton(onClick = {
                                dLog { "alarmClockInfo>>>>>click add alarm start" }
                                setAlarm.launchMultiplePermissionRequest()
                                state.launchMultiplePermissionRequest()
                                viewModel.addEvent()
                                viewModel.addAlarm(this@MainActivity)
//                                showDialog.value = true
                            }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "")
                            }
                            IconButton(onClick = {
                                viewModel.deleteEvent()
//                                viewModel.deleteAlarm()
                            }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                            }
                            IconButton(onClick = {
                                state.launchMultiplePermissionRequest()
                                viewModel.requestEvents()
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
                if (showDialog.value) {
                    ShowDialog(viewModel) {
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDialog(viewModel: AlarmViewModel, dismiss: () -> Unit) {
    val titleState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    val startTimeState = rememberDatePickerState()
    val endTimeState = rememberDatePickerState()
    Dialog(onDismissRequest = {
        dismiss()
    }) {
        Column {
            OutlinedTextField(value = titleState.value, onValueChange = {
                titleState.value = it
            })
            OutlinedTextField(value = descriptionState.value, onValueChange = {
                descriptionState.value = it
            })
            DatePicker(startTimeState)
            DatePicker(endTimeState)
        }
    }
}

@Composable
private fun MainActivity.ContextView(modifier: Modifier, viewModel: AlarmViewModel) {
    Box(modifier = modifier) {
        val eventsState = viewModel.eventsState
        val result = eventsState.value
        if (result is ResultData.Success) {
            Column(
                modifier = modifier
                    .align(Alignment.Center)
                    .verticalScroll(
                        rememberScrollState(),
                    )
            ) {
                result.data.forEach {
                    ItemView(it, viewModel = viewModel)
                }
            }

        } else if (result is ResultData.Failure) {
            Text(text = result.error.message ?: "", modifier = Modifier.align(Alignment.Center))
        } else {
            //loading
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun ItemView(event: Event, viewModel: AlarmViewModel) {
    Row {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(text = event.title)
            Text(text = formatTime(event.startTime))
            Text(text = event.description ?: "")

        }
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            IconButton(onClick = {
                viewModel.deleteEvent(event.id)
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "")

            }

        }
    }

}

 fun formatTime(lng: Long): String {
    //格式化日期
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(lng)
}
