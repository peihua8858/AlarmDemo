package com.peihua.alarmdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.peihua.alarmdemo.ui.theme.AlermDemoTheme
import com.peihua.alarmdemo.utils.dLog

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dLog { "alarmClockInfo>>>>>onCreate" }
        setContent {
            AlermDemoTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Text(text = "SecondActivity")
                }
            }
        }
    }
}