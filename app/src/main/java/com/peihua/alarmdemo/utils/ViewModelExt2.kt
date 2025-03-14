package com.peihua.alarmdemo.utils

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * ViewModel 状态记录
 * @author dingpeihua
 * @date 2021/2/20 11:28
 * @version 1.0
 */
sealed class ResultData<T> {
    class Starting<T> : ResultData<T>()
    data class Success<T>(val data: T) : ResultData<T>()
    data class Failure<T>(val error: Throwable) : ResultData<T>()
}
sealed class ResultData1<T>(val value: T) {
    class Starting : ResultData1<Float>(1f)
     class Success: ResultData1<Int>(33)
     class Failure : ResultData1<String>("FunnySaltyFish")
}

fun <V> unboxAndProcess(container: ResultData1<V>): V =
    when (container) {
        is ResultData1.Success -> container.value // V 会被推断为 Int
        is ResultData1.Starting -> container.value // V 会被推断为 String
        is ResultData1.Failure -> container.value // V 会被推断为 String
    }

private const val TAG = "ResultData"

@OptIn(ExperimentalContracts::class)
fun <T> ResultData<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is ResultData.Success)
    }
    return this is ResultData.Success
}

@OptIn(ExperimentalContracts::class)
fun <T> ResultData<T>.isError(): Boolean {
    contract {
        returns(true) implies (this@isError is ResultData.Failure)
    }
    return this is ResultData.Failure
}

@OptIn(ExperimentalContracts::class)
fun <T> ResultData<T>.isStarting(): Boolean {
    contract {
        returns(true) implies (this@isStarting is ResultData.Starting)
    }
    return this is ResultData.Starting
}

fun <T> ViewModel.apiSyncRequest(
    apiDSL: ApiModel<T>.() -> Unit,
) {
    ApiModel<T>().apply(apiDSL)
        .syncLaunch(viewModelScope)

}

/**
 * [ViewModel]在IO线程中开启协程扩展
 */
fun <T> ViewModel.request(
    viewState: MutableState<ResultData<T>>,
    request: suspend CoroutineScope.() -> T,
) {
    viewModelScope.launch(Dispatchers.Main) {
        viewState.value = ResultData.Starting()
        try {
            val response = withContext(Dispatchers.IO) {
                request()
            }
            viewState.value = ResultData.Success(response)
        } catch (e: Throwable) {
            print(e.stackTraceToString())
            viewState.value = ResultData.Failure(e)
        }
    }
}
