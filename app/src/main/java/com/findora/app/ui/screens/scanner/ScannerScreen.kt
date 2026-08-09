package com.findora.app.ui.screens.scanner

import android.Manifest
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.findora.app.R
import com.findora.app.ui.components.EmptyState
import com.findora.app.ui.components.PrimaryButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onBack: () -> Unit,
    onScanned: (Long) -> Unit,
    viewModel: ScannerViewModel = viewModel(factory = ScannerViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Navigate to the new document once processing succeeds.
    LaunchedEffect(state) {
        (state as? ScanState.Success)?.let { onScanned(it.documentId) }
    }

    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { viewModel.process(it) }
        }
    }
    fun startCrop(source: Uri) {
        cropLauncher.launch(CropImageContractOptions(source, CropImageOptions()))
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> uri?.let { startCrop(it) } }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (cameraPermission.status.isGranted) {
            CameraLayer(
                onCaptured = { startCrop(it) },
                onPickGallery = {
                    galleryLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
                onBack = onBack,
            )
        } else {
            PermissionRequest(
                onGrant = { cameraPermission.launchPermissionRequest() },
                onBack = onBack,
            )
        }

        if (state is ScanState.Processing) {
            ProcessingOverlay()
        }
        (state as? ScanState.Error)?.let { err ->
            ErrorOverlay(message = err.message, onDismiss = viewModel::dismissError)
        }
    }
}

@Composable
private fun CameraLayer(
    onCaptured: (Uri) -> Unit,
    onPickGallery: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var flashOn by remember { mutableStateOf(false) }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }
    LaunchedEffect(Unit) { controller.bindToLifecycle(lifecycleOwner) }
    LaunchedEffect(flashOn) {
        controller.imageCaptureFlashMode =
            if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = controller
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Top bar: back + flash
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OverlayIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Back", onBack)
            OverlayIconButton(
                if (flashOn) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                stringResource(R.string.flash),
            ) { flashOn = !flashOn }
        }

        // Bottom controls: gallery + capture
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OverlayIconButton(Icons.Rounded.PhotoLibrary, stringResource(R.string.gallery), onPickGallery)
            CaptureButton {
                captureTo(context, controller, onCaptured)
            }
            Spacer(Modifier.size(48.dp)) // balance the row
        }
    }
}

private fun captureTo(
    context: android.content.Context,
    controller: LifecycleCameraController,
    onCaptured: (Uri) -> Unit,
) {
    val file = File.createTempFile("findora_capture", ".jpg", context.cacheDir)
    val output = ImageCapture.OutputFileOptions.Builder(file).build()
    controller.takePicture(
        output,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                onCaptured(results.savedUri ?: Uri.fromFile(file))
            }

            override fun onError(exception: ImageCaptureException) {
                // Surfacing camera errors is out of scope; the user can retry.
            }
        },
    )
}

@Composable
private fun CaptureButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        modifier = Modifier.size(76.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(60.dp)) {}
        }
    }
}

@Composable
private fun OverlayIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.4f),
        modifier = Modifier.size(48.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, tint = Color.White)
        }
    }
}

@Composable
private fun ProcessingOverlay() {
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.White)
            Spacer(Modifier.size(16.dp))
            Text(stringResource(R.string.recognizing), color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ErrorOverlay(message: String, onDismiss: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Scan failed", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.size(8.dp))
                Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.size(20.dp))
                PrimaryButton(text = "Try again", onClick = onDismiss)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRequest(onGrant: () -> Unit, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
        }
        EmptyState(
            icon = Icons.Rounded.PhotoLibrary,
            title = stringResource(R.string.grant_permission),
            body = stringResource(R.string.camera_permission_rationale),
            actionLabel = stringResource(R.string.grant_permission),
            onAction = onGrant,
        )
    }
}
