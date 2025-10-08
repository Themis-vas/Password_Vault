package com.passguard.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.passguard.app.core.crypto.BiometricCryptoManager
import com.passguard.app.core.designsystem.theme.PassGuardTheme
import com.passguard.app.domain.model.LockState
import com.passguard.app.ui.AppViewModel
import com.passguard.app.ui.PassGuardNavHost
import com.passguard.app.ui.lock.LockScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    @Inject
    lateinit var biometricCryptoManager: BiometricCryptoManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { appViewModel.isLoading.value }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val lockState by appViewModel.lockState.collectAsStateWithLifecycle()
            val settings by appViewModel.settings.collectAsStateWithLifecycle()
            val windowSizeClass = calculateWindowSizeClass(this)
            val isExpanded = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
            val navController = rememberNavController()

            LaunchedEffect(settings.secureScreenEnabled) {
                if (settings.secureScreenEnabled) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            PassGuardTheme(darkTheme = settings.useDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (lockState) {
                        LockState.Locked -> {
                            LockScreen(
                                lockState = lockState,
                                canUseBiometric = biometricCryptoManager.canAuthenticate(),
                                onUnlock = { pin, result -> appViewModel.unlockWithPin(pin, result) },
                                onCreatePin = { pin, result -> appViewModel.createPin(pin, result) },
                                onAuthenticateBiometric = { authenticateBiometric() }
                            )
                        }

                        LockState.NoPinSet -> {
                            LockScreen(
                                lockState = lockState,
                                canUseBiometric = false,
                                onUnlock = { _, _ -> },
                                onCreatePin = { pin, result -> appViewModel.createPin(pin, result) },
                                onAuthenticateBiometric = {}
                            )
                        }

                        LockState.Unlocked -> {
                            PassGuardNavHost(
                                navController = navController,
                                isExpanded = isExpanded,
                                appViewModel = appViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        appViewModel.handleAppBackground()
    }

    private fun authenticateBiometric() {
        lifecycleScope.launch {
            runCatching { biometricCryptoManager.authenticate(this@MainActivity) }
                .onSuccess { success -> if (success) appViewModel.unlockBiometric() }
        }
    }
}
