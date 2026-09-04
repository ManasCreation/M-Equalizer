package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.DeviceSelector
import com.example.ui.components.LimiterMeter
import com.example.ui.components.LiveAudioVisualizer
import com.example.ui.components.PresetChips
import com.example.ui.components.SoundEnhancementControls
import com.example.ui.components.TenBandEqualizer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPillBg
import com.example.ui.theme.CyanPillBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ImmersiveBorderMedium
import com.example.ui.theme.ImmersiveBorderSubtle
import com.example.ui.theme.ImmersiveCanvasBg
import com.example.ui.theme.ImmersiveCardBg
import com.example.ui.theme.ImmersiveCardBgTranslucent
import com.example.ui.theme.ImmersiveInnerBg
import com.example.ui.theme.ImmersiveSlate800
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EqualizerScreen(
    viewModel: EqualizerViewModel,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val selectedDeviceType by viewModel.selectedDeviceType.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val spectrumData by viewModel.spectrumData.collectAsStateWithLifecycle()
    val peakData by viewModel.peakData.collectAsStateWithLifecycle()
    val waveformData by viewModel.waveformData.collectAsStateWithLifecycle()

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val isMasterOn = currentProfile.isEnabled
    val masterGlowColor by animateColorAsState(
        targetValue = if (isMasterOn) CyanPrimary else ImmersiveBorderMedium,
        label = "master_power_glow"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = ImmersiveCanvasBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(
                top = statusBarPadding + 14.dp,
                bottom = navBarPadding + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Immersive Top App Bar & Glowing Master Power Switch
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "M-Equalizer",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "PRO AUDIO ENGINE V4.2",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp,
                            color = CyanPrimary.copy(alpha = 0.85f)
                        )
                    }

                    // Immersive Rounded Power Button with Soft Cyan Aura
                    Box(contentAlignment = Alignment.Center) {
                        if (isMasterOn) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimary.copy(alpha = 0.2f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ImmersiveSlate800.copy(alpha = 0.9f))
                                .border(
                                    width = 1.2.dp,
                                    color = if (isMasterOn) CyanPrimary.copy(alpha = 0.6f) else ImmersiveBorderMedium,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.toggleMasterPower(!isMasterOn) }
                                .testTag("master_power_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isMasterOn) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(CyanGlow)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(TextMuted.copy(alpha = 0.4f))
                                )
                            }
                        }
                    }
                }
            }

            // 2. Real-Time Spectrum Visualizer (Immersive Card rounded-3xl)
            item {
                LiveAudioVisualizer(
                    spectrumData = spectrumData,
                    peakData = peakData,
                    waveformData = waveformData,
                    isEngineActive = isMasterOn
                )
            }

            // 3. 10-Band Studio Equalizer Filter Bank
            item {
                TenBandEqualizer(
                    bands = currentProfile.eqBands,
                    presetName = currentProfile.presetName,
                    onBandChanged = { index, valueDb -> viewModel.updateEqBand(index, valueDb) }
                )
            }

            // 4. Pro Sound Engine Enhancements: Dials (Bass Boost, 3D Spatial, Loudness) & Reverb
            item {
                SoundEnhancementControls(
                    bassBoost = currentProfile.bassBoost,
                    onBassBoostChanged = { viewModel.updateBassBoost(it) },
                    loudnessGain = currentProfile.loudnessGain,
                    onLoudnessGainChanged = { viewModel.updateLoudnessGain(it) },
                    spatialSurround = currentProfile.spatialSurround,
                    onSpatialSurroundChanged = { viewModel.updateSpatialSurround(it) },
                    reverbPreset = currentProfile.reverbPreset,
                    onReverbPresetChanged = { viewModel.updateReverbPreset(it) }
                )
            }

            // 5. Studio Presets Selector Chips
            item {
                PresetChips(
                    selectedPresetName = currentProfile.presetName,
                    onPresetSelected = { viewModel.applyPreset(it) },
                    onResetToFlat = { viewModel.resetToFlat() }
                )
            }

            // 6. Anti-Clipping & Dynamics Processing (Brickwall Limiter + AGC)
            item {
                LimiterMeter(
                    limiterEnabled = currentProfile.limiterEnabled,
                    onLimiterToggled = { viewModel.toggleLimiter(it) },
                    agcEnabled = currentProfile.agcEnabled,
                    onAgcToggled = { viewModel.toggleAgc(it) },
                    reductionDb = audioState.limiterReductionDb,
                    peakOutputDb = audioState.peakOutputDb
                )
            }

            // 7. Multi-Device Profile Manager (Bottom / Footer row)
            item {
                DeviceSelector(
                    selectedDeviceType = selectedDeviceType,
                    onDeviceSelected = { viewModel.selectDevice(it) }
                )
            }

            // 8. Footer Credit
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.testTag("creator_credit_text"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Designed & Developed by ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.4.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Manas Mete",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.4.sp,
                            color = CyanPrimary.copy(alpha = 0.85f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100% Offline • Zero-Latency Native DSP",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
