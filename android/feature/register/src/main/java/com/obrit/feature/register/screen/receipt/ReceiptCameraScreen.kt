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
import kotlinx.coroutines.delay
import java.io.File

/**
 * 영수증 촬영 화면.
 *
 * 진입 시 안내 문구를 보여주고, 잠시 후 컷아웃 뷰파인더로 전환한다(타이머).
 * 갤러리 선택 / 직접 촬영 / 전후면 전환 / 좌상단 X(→홈) 동작을 제공한다.
 *
 * 범위는 UI-only. 촬영·선택 결과 [Uri]는 보관만 하며 업로드 연동은 차후 작업이다.
 * 분석 API 연동 전까지는 임시 딜레이 후 [onAnalysisComplete]로 결과 화면 이동만 수행한다.
 */
@Composable
fun ReceiptCameraScreen(
    onClose: () -> Unit,
    onAnalysisComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(context.hasCameraPermission()) }
    var showGuide by remember { mutableStateOf(true) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }

    val onImageReady: (Uri) -> Unit = { uri ->
        capturedUri = uri
        isAnalyzing = true
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(RequestPermission()) { hasCameraPermission = it }
    val galleryLauncher =
        rememberLauncherForActivityResult(PickVisualMedia()) { if (it != null) onImageReady(it) }

    ReceiptCameraEffects(
        hasCameraPermission = hasCameraPermission,
        isAnalyzing = isAnalyzing,
        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
        onGuideComplete = { showGuide = false },
        onAnalysisComplete = onAnalysisComplete,
    )
    ReceiptCameraContent(
        hasCameraPermission = hasCameraPermission,
        showGuide = showGuide,
        lensFacing = lensFacing,
        capturedUri = capturedUri,
        isAnalyzing = isAnalyzing,
        onClose = onClose,
        onGalleryLaunch = { galleryLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
        onCapture = { imageCapture?.captureToCache(context) { uri -> onImageReady(uri) } },
        onFlip = { lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK },
        onImageCaptureReady = { imageCapture = it },
        modifier = modifier,
    )
}

@Composable
private fun ReceiptCameraEffects(
    hasCameraPermission: Boolean,
    isAnalyzing: Boolean,
    onRequestPermission: () -> Unit,
    onGuideComplete: () -> Unit,
    onAnalysisComplete: () -> Unit,
) {
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) onRequestPermission()
    }
    LaunchedEffect(Unit) {
        delay(GUIDE_DURATION_MILLIS)
        onGuideComplete()
    }
    // 분석 API 연동 시 임시 딜레이를 실제 응답 처리로 교체.
    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            delay(ANALYZING_MOCK_DURATION_MILLIS)
            onAnalysisComplete()
        }
    }
}

@Composable
private fun ReceiptCameraContent(
    hasCameraPermission: Boolean,
    showGuide: Boolean,
    lensFacing: Int,
    capturedUri: Uri?,
    isAnalyzing: Boolean,
    onClose: () -> Unit,
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
        val analyzingUri = capturedUri
        if (isAnalyzing && analyzingUri != null) {
            ReceiptAnalyzingOverlay(imageUri = analyzingUri, modifier = Modifier.fillMaxSize())
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

private const val GUIDE_DURATION_MILLIS = 2000L
private const val ANALYZING_MOCK_DURATION_MILLIS = 2000L
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
