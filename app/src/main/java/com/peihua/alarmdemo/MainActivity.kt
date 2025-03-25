package com.peihua.alarmdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.peihua.alarmdemo.ui.theme.AlermDemoTheme
import com.peihua.alarmdemo.utils.Logcat
import com.peihua.alarmdemo.utils.ResultData
import com.peihua.alarmdemo.utils.dLog
import com.peihua8858.permissions.core.requestPermissions
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dLog { "alarmClockInfo>>>>>onCreate" }
        enableEdgeToEdge()
        requestPermissions(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
//            Manifest.permission.SET_ALARM,
//            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        ) {
            onGranted {
                setContent {
                    ContentView()
                }
            }
            onShowRationale {
                setContent {
                    ShowDialog({
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "需要权限获取日程数据，及管理日程等",
                                fontSize = 18.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }, positiveText = "去设置", negativeText = "取消") {
                        onPositive {
                            //去设置
                            val uri = Uri.parse("package:$packageName")
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                            startActivity(intent)
                        }
                    }
                }
            }
            onDenied {
                setContent {
                    ShowDialog({
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "请先授予必要权限",
                                fontSize = 18.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }, positiveText = "去设置", negativeText = "取消") {
                        onPositive {
                            Logcat.d("alarmClockInfo>>>>>去设置")
                            val uri = Uri.parse("package:$packageName")
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                            startActivity(intent)
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun MainActivity.ContentView(viewModel: AlarmViewModel = viewModel()) {
    AlermDemoTheme {
        val showDialog = remember { mutableStateOf(false) }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
            },
            floatingActionButton = {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.White)
                ) {
                    IconButton(onClick = {
                        dLog { "alarmClockInfo>>>>>click add alarm start" }
                        viewModel.addEvent()
//                        viewModel.addAlarm(this@ContentView)
//                                showDialog.value = true
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    }
                    IconButton(onClick = {
                        viewModel.deleteEvent()
//                        viewModel.deleteAlarm()
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                    }
                    IconButton(onClick = {
                        viewModel.requestEvents()
//                        viewModel.requestAlarm()
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
//            ShowDialog(viewModel) {
//                showDialog.value = false
//            }
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
