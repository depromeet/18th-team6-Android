package com.obrit.feature.register.screen.receipt

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * CameraX 프리뷰를 화면 맨 아래 레이어로 임베드한다.
 * UI(상단 바·오버레이·컨트롤)는 이 위에 Compose로 직접 그린다.
 *
 * @param lensFacing 전/후면 선택. 값이 바뀌면 유스케이스를 재바인딩한다.
 * @param onImageCaptureReady 바인딩된 [ImageCapture] 핸들을 부모로 올려 셔터에서 사용한다.
 */
@Composable
internal fun CameraPreview(
    lensFacing: Int,
    onImageCaptureReady: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val currentOnImageCaptureReady by rememberUpdatedState(onImageCaptureReady)

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.awaitCameraProvider()
        val preview =
            Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
        val selector =
            CameraSelector
                .Builder()
                .requireLensFacing(lensFacing)
                .build()

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
        currentOnImageCaptureReady(imageCapture)
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier,
    )
}

private suspend fun android.content.Context.awaitCameraProvider(): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            { continuation.resume(future.get()) },
            ContextCompat.getMainExecutor(this),
        )
    }
