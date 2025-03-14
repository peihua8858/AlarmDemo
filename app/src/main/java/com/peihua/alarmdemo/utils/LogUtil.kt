package com.peihua.alarmdemo.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private const val STACK_TRACE_INDEX = 6

@Composable
fun writeLog(tagStr: String, log: String) {
    val context = LocalContext.current
    Logcat.writeLog(context, STACK_TRACE_INDEX, tagStr, log)
}

@Composable
fun writeLog(lazyMessage: () -> String) {
    val context = LocalContext.current
    Logcat.writeLog(context, STACK_TRACE_INDEX, null, lazyMessage())
}

@Composable
fun Context.writeLog(tagStr: String, lazyMessage: () -> String) {
    Logcat.writeLog(this, STACK_TRACE_INDEX, tagStr, lazyMessage())
}
@Composable
fun  ALog(lazyMessage: () -> Any) {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.A, null, message)
}
fun <T> T.aLog(lazyMessage: () -> Any): T {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.A, null, message)
    return this
}
@Composable
fun  VLog(lazyMessage: () -> Any) {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.V, null, message)
}
fun <T> T.vLog(lazyMessage: () -> Any): T {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.V, null, message)
    return this
}

fun <T> T.jsonLog(lazyMessage: () -> Any): T {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.JSON, null, message)
    return this
}

fun <T> T.xmlLog(lazyMessage: () -> Any): T {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.XML, null, message)
    return this
}

fun <T> T.wLog(lazyMessage: () -> Any): T {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.W, null, message)
    return this
}
@Composable
fun  ELog(lazyMessage: () -> Any) {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.E, null, message)
}
fun <T> T.eLog(lazyMessage: () -> Any): T {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.E, null, message)
    return this
}
@Composable
fun  DLog(lazyMessage: () -> Any) {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.D, null, message)
}
fun <T> T.dLog(lazyMessage: () -> Any): T {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.D, null, message)
    return this
}

fun <T> T.iLog(lazyMessage: () -> Any): T {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.I, null, message)
    return this
}

fun Int.dLog(lazyMessage: () -> Any): Int {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.D, null, message)
    return this
}

fun Double.dLog(lazyMessage: () -> Any): Double {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.D, null, message)
    return this
}

fun Float.dLog(lazyMessage: () -> Any): Float {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.D, null, message)
    return this
}

fun Long.dLog(lazyMessage: () -> Any): Long {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.D, null, message)
    return this
}


fun Int.eLog(lazyMessage: () -> Any): Int {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.D, null, message)
    return this
}

fun Double.eLog(lazyMessage: () -> Any): Double {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.E, null, message)
    return this
}

fun Float.eLog(lazyMessage: () -> Any): Float {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.E, null, message)
    return this
}

fun Long.eLog(lazyMessage: () -> Any): Long {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.E, null, message)
    return this
}

fun Int.wLog(lazyMessage: () -> Any): Int {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.W, null, message)
    return this
}

fun Double.wLog(lazyMessage: () -> Any): Double {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.W, null, message)
    return this
}

fun Float.wLog(lazyMessage: () -> Any): Float {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.W, null, message)
    return this
}

fun Long.wLog(lazyMessage: () -> Any): Long {
    val message = lazyMessage()
    Logcat.printLog(STACK_TRACE_INDEX, Logcat.W, null, message)
    return this
}