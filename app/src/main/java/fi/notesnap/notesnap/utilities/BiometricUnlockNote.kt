package fi.notesnap.notesnap.utilities

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

class BiometricUnlockNote(
    context: Context,
    fragment: FragmentActivity,
    toggleNoteDetails: (Boolean) -> Unit
) {
    private var executor: Executor
    private var biometricPrompt: BiometricPrompt
    private var promptInfo: BiometricPrompt.PromptInfo

    private var authenticationSucceeded: Boolean = false

    init {
        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(
            fragment, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        context,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                    authenticationSucceeded = false
                    toggleNoteDetails(false)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        context,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    )
                        .show()
                    authenticationSucceeded = true
                    toggleNoteDetails(true)

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    authenticationSucceeded = false
                    toggleNoteDetails(false)

                }
            })
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Lock")
            .setSubtitle("Unlock the note using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()
    }

    fun checkDeviceHasBiometric(context: Context) {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("BIOMETRIC", "App can authenticate using biometric")
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d("BIOMETRIC", "No Hardware")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.d("BIOMETRIC", "Biometrics is currently unavailable")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.d("BIOMETRIC", "Device does not enable biometric feature")
            }

            else -> {
                Log.d("BIOMETRIC", "Something went wrong")
            }

        }
    }

    fun authenticate() {
        biometricPrompt.authenticate(promptInfo)
    }
}