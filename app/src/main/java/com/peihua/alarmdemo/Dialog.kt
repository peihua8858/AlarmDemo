package com.peihua.alarmdemo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun rememberDialogShowing(isShowing: Boolean) = remember { mutableStateOf(isShowing) }

@Composable
fun ShowDialog(
    content: @Composable () -> Unit,
    dialogState: MutableState<Boolean> = rememberDialogShowing(true),
    positiveText: String? = null,
    negativeText: String? = null,
    neutralText: String? = null,
    properties: DialogProperties = DialogProperties(),
    apiDSL: DialogApiModel.() -> Unit,
) {
    val apiModel = DialogApiModel().apply(apiDSL)
    if (dialogState.value == false) {
        return
    }
    Dialog(onDismissRequest = {
        dialogState.value = false
        apiModel.invokeOnDismiss()
    }, properties = properties) {
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Column(modifier = Modifier.weight(1f)) {
                content()
            }
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .padding(top = 16.dp)
            ) {
                if (!negativeText.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable {
                                apiModel.invokeOnNegative()
                                dialogState.value = false
                            },
                        contentAlignment = Alignment.Center // 垂直和水平居中
                    ) {
                        Text(
                            text = negativeText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (!neutralText.isNullOrBlank()) {
                    if (!negativeText.isNullOrBlank()) {
                        VerticalDivider()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable {
                                apiModel.invokeOnNeutral()
                                dialogState.value = false
                            },
                        contentAlignment = Alignment.Center // 垂直和水平居中
                    ) {
                        Text(
                            text = neutralText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (!positiveText.isNullOrBlank()) {
                    if (!negativeText.isNullOrBlank() || !neutralText.isNullOrBlank()) {
                        VerticalDivider()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable {
                                apiModel.invokeOnPositive()
                                dialogState.value = false
                            },
                        contentAlignment = Alignment.Center // 垂直和水平居中
                    ) {
                        Text(
                            text = positiveText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

    }
}

class DialogApiModel() {
    private var onDismiss: (() -> Unit?)? = null
    private var onPositive: (() -> Unit?)? = null
    private var onNegative: (() -> Unit?)? = null
    private var onNeutral: (() -> Unit?)? = null
    infix fun onDismiss(onDismiss: (() -> Unit?)?): DialogApiModel {
        this.onDismiss = onDismiss
        return this
    }

    infix fun onPositive(onPositive: (() -> Unit?)?): DialogApiModel {
        this.onPositive = onPositive
        return this
    }


    infix fun onNegative(onNegative: (() -> Unit)?): DialogApiModel {
        this.onNegative = onNegative
        return this
    }

    infix fun onNeutral(onNeutral: (() -> Unit)?): DialogApiModel {
        this.onNeutral = onNeutral
        return this
    }

    fun invokeOnNegative() {
        onNegative?.invoke()
    }

    fun invokeOnNeutral() {
        this.onNeutral?.invoke()
    }

    fun invokeOnPositive() {
        this.onPositive?.invoke()
    }

    fun invokeOnDismiss() {
        this.onDismiss?.invoke()
    }
}