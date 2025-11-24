package ai.cargominds.ktorutils.firebase.ktx

import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.ExecutionException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

internal suspend fun <T> ApiFuture<T>.await(): T {
    try {
        @Suppress("BlockingMethodInNonBlockingContext")
        if (isDone) return get() as T
    } catch (e: ExecutionException) {
        throw e.cause ?: e // unwrap original cause from ExecutionException
    }

    return suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
        val callback = ContinuationCallback(cont)
        ApiFutures.addCallback(this, callback, MoreExecutors.directExecutor())
        cont.invokeOnCancellation {
            cancel(false)
            callback.cont = null // clear the reference to continuation from the future's callback
        }
    }
}

private class ContinuationCallback<T>(
    @Volatile @JvmField var cont: Continuation<T>?,
) : ApiFutureCallback<T> {
    override fun onSuccess(result: T?) {
        @Suppress("UNCHECKED_CAST")
        cont?.resume(result as T)
    }

    override fun onFailure(t: Throwable) {
        cont?.resumeWithException(t)
    }
}
