package com.obrit.feature.register.screen.receipt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.feature.register.viewmodel.ReceiptCameraSideEffect
import com.obrit.feature.register.viewmodel.ReceiptCameraViewModel
import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.io.File

/**
 * 영수증 촬영 화면.
 *
 * 진입 시 안내 문구를 보여주고, 잠시 후 컷아웃 뷰파인더로 전환한다(타이머).
 * 갤러리 선택 / 직접 촬영 / 전후면 전환 / 좌상단 X(→홈) 동작을 제공한다.
 *
 * 이미지가 확정되면 [ReceiptCameraViewModel]이 영수증 분석 API를 호출하고,
 * 성공 시 [onAnalysisComplete]로 결과를 전달한다. 실패 시 수동 재시도 오버레이를 보여준다.
 */
@Composable
fun ReceiptCameraScreen(
    onClose: () -> Unit,
    onAnalysisComplete: (ReceiptAnalysis) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptCameraViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.collectAsState()
    var hasCameraPermission by remember { mutableStateOf(context.hasCameraPermission()) }
    var showGuide by remember { mutableStateOf(true) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var retryToken by remember { mutableIntStateOf(0) }
    // 이미지 확정 직후부터 분석 API 호출 전(다운스케일·WebP 인코딩) 구간에도 오버레이를 띄우기 위한 로컬 플래그.
    var isProcessing by remember { mutableStateOf(false) }

    val onImageReady: (Uri) -> Unit = { uri ->
        capturedUri = uri
        isProcessing = true
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(RequestPermission()) { hasCameraPermission = it }
    val galleryLauncher =
        rememberLauncherForActivityResult(PickVisualMedia()) { if (it != null) onImageReady(it) }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    LaunchedEffect(Unit) {
        delay(GUIDE_DURATION_MILLIS)
        showGuide = false
    }
    // 이미지 확정/재시도 시 업로드용으로 다운스케일·압축한 bytes를 읽어 분석을 시작한다.
    // 자동 루프 없이 retryToken으로만 재실행.
    LaunchedEffect(capturedUri, retryToken) {
        val uri = capturedUri ?: return@LaunchedEffect
        val bytes = withContext(Dispatchers.IO) { context.readReceiptImage(uri) }
        if (bytes == null) {
            isProcessing = false
            return@LaunchedEffect
        }
        viewModel.analyze(bytes, uri.toReceiptFileName())
    }
    // 분석 실패가 확정되면 에러 오버레이가 보이도록 진행 중 플래그를 내린다.
    LaunchedEffect(state.isError) {
        if (state.isError) isProcessing = false
    }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ReceiptCameraSideEffect.OnAnalyzed -> onAnalysisComplete(sideEffect.analysis)
        }
    }

    ReceiptCameraContent(
        hasCameraPermission = hasCameraPermission,
        showGuide = showGuide,
        lensFacing = lensFacing,
        capturedUri = capturedUri,
        isAnalyzing = isProcessing || state.isAnalyzing,
        isError = state.isError,
        onClose = onClose,
        onRetry = {
            viewModel.onErrorDismiss()
            isProcessing = true
            retryToken++
        },
        onGalleryLaunch = { galleryLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
        onCapture = { imageCapture?.captureToCache(context) { uri -> onImageReady(uri) } },
        onFlip = { lensFacing = lensFacing.toggledLens() },
        onImageCaptureReady = { imageCapture = it },
        modifier = modifier,
    )
}

@Composable
private fun ReceiptCameraContent(
    hasCameraPermission: Boolean,
    showGuide: Boolean,
    lensFacing: Int,
    capturedUri: Uri?,
    isAnalyzing: Boolean,
    isError: Boolean,
    onClose: () -> Unit,
    onRetry: () -> Unit,
    onGalleryLaunch: () -> Unit,
    onCapture: () -> Unit,
    onFlip: () -> Unit,
    onImageCaptureReady: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            CameraPreview(
                lensFacing = lensFacing,
                onImageCaptureReady = onImageCaptureReady,
                modifier = Modifier.fillMaxSize(),
            )
        }
        ReceiptCameraGuideOrScan(showGuide = showGuide)
        ReceiptCameraTopBar(
            onClose = onClose,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars),
        )
        ReceiptCameraControls(
            onGalleryClick = onGalleryLaunch,
            onShutterClick = onCapture,
            onFlipClick = onFlip,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = CONTROLS_BOTTOM_PADDING.dp),
        )
        val overlayUri = capturedUri
        if (overlayUri != null && isAnalyzing) {
            ReceiptAnalyzingOverlay(imageUri = overlayUri, modifier = Modifier.fillMaxSize())
        }
        if (overlayUri != null && isError) {
            ReceiptAnalyzeErrorOverlay(
                imageUri = overlayUri,
                onRetry = onRetry,
                onClose = onClose,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ReceiptCameraGuideOrScan(showGuide: Boolean) {
    if (showGuide) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = GUIDE_DIM_ALPHA)),
        )
        // 컷아웃과 동일한 안전영역(상단 바 아래 ~ 하단 컨트롤 위) 중앙에 배치한다.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(
                        top = TOP_BAR_HEIGHT.dp,
                        bottom = (CONTROLS_BLOCK_HEIGHT + CONTROLS_BOTTOM_PADDING).dp,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            ReceiptGuide()
        }
    } else {
        ReceiptScanOverlay()
    }
}

@Composable
private fun ReceiptCameraTopBar(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .size(TOP_BAR_HEIGHT.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = TOP_BAR_SIDE_PADDING.dp)
                    .size(TOP_BAR_ICON_BUTTON.dp)
                    .clip(RoundedCornerShape(TOP_BAR_ICON_RADIUS.dp))
                    .clickable(role = Role.Button, onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_topbar_close),
                contentDescription = CLOSE_DESCRIPTION,
                tint = colors.common00,
                modifier = Modifier.size(TOP_BAR_ICON.dp),
            )
        }
        Text(
            text = TITLE,
            style = typography.xl.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun ReceiptGuide(modifier: Modifier = Modifier) {
    val typography = LocalOBRitTypography.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GUIDE_GAP.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_receipt_guide),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(GUIDE_ICON_SIZE.dp),
        )
        Text(
            text = GUIDE_TEXT,
            style = typography.xl5.copy(fontWeight = FontWeight.Medium),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

private fun Context.hasCameraPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

private fun Int.toggledLens(): Int =
    if (this == CameraSelector.LENS_FACING_BACK) {
        CameraSelector.LENS_FACING_FRONT
    } else {
        CameraSelector.LENS_FACING_BACK
    }

private fun ImageCapture.captureToCache(
    context: Context,
    onSaved: (Uri) -> Unit,
) {
    val file = File(context.cacheDir, "receipt_${System.currentTimeMillis()}.jpg")
    val options = ImageCapture.OutputFileOptions.Builder(file).build()
    takePicture(
        options,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSaved(outputFileResults.savedUri ?: file.toUri())
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(LOG_TAG, "capture failed", exception)
            }
        },
    )
}

// 업로드 바이트는 항상 JPEG로 재인코딩되므로 확장자도 .jpg로 맞춘다.
private fun Uri.toReceiptFileName(): String {
    val base = lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: "receipt"
    return "$base.jpg"
}

private const val GUIDE_DURATION_MILLIS = 2000L
private const val GUIDE_DIM_ALPHA = 0.6f
private const val TOP_BAR_HEIGHT = 56
private const val TOP_BAR_SIDE_PADDING = 12
private const val TOP_BAR_ICON_BUTTON = 40
private const val TOP_BAR_ICON_RADIUS = 16
private const val TOP_BAR_ICON = 24
private const val CONTROLS_BOTTOM_PADDING = 65
private const val CONTROLS_BLOCK_HEIGHT = 68
private const val GUIDE_GAP = 20
private const val GUIDE_ICON_SIZE = 68

private const val LOG_TAG = "ReceiptCamera"
private const val TITLE = "영수증 촬영"
private const val CLOSE_DESCRIPTION = "닫기"
private const val GUIDE_TEXT = "직접 구매한 마트 영수증의\n구매한 소모품 정보가\n잘 나오도록 찍어주세요"
