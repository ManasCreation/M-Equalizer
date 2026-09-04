package com.example.data.model

data class AudioPreset(
    val name: String,
    val bands: List<Int>, // 10 bands from -15 to +15 dB
    val bassBoost: Int = 0, // 0 to 100
    val loudnessGain: Int = 0, // 0 to 100
    val spatialSurround: Int = 0, // 0 to 100
    val reverbPreset: Int = 0 // 0: None, 1: Small Room, etc.
) {
    companion object {
        val FREQUENCY_LABELS = listOf(
            "31Hz", "62Hz", "125Hz", "250Hz", "500Hz",
            "1kHz", "2kHz", "4kHz", "8kHz", "16kHz"
        )

        val FREQUENCY_VALUES = listOf(
            31, 62, 125, 250, 500,
            1000, 2000, 4000, 8000, 16000
        )

        val BUILT_IN_PRESETS = listOf(
            AudioPreset(
                name = "Flat / Direct",
                bands = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                bassBoost = 0,
                loudnessGain = 0,
                spatialSurround = 0,
                reverbPreset = 0
            ),
            AudioPreset(
                name = "Studio Master",
                bands = listOf(3, 2, 1, 0, -1, 1, 2, 3, 4, 3),
                bassBoost = 25,
                loudnessGain = 20,
                spatialSurround = 35,
                reverbPreset = 1
            ),
            AudioPreset(
                name = "Bass Heavy",
                bands = listOf(9, 8, 6, 4, 1, 0, 1, 2, 3, 3),
                bassBoost = 80,
                loudnessGain = 35,
                spatialSurround = 20,
                reverbPreset = 0
            ),
            AudioPreset(
                name = "Vocal Booster",
                bands = listOf(-2, -1, 0, 2, 5, 6, 5, 3, 1, 0),
                bassBoost = 10,
                loudnessGain = 15,
                spatialSurround = 25,
                reverbPreset = 1
            ),
            AudioPreset(
                name = "Electronic / EDM",
                bands = listOf(7, 6, 4, 1, -1, 2, 4, 6, 7, 6),
                bassBoost = 65,
                loudnessGain = 40,
                spatialSurround = 50,
                reverbPreset = 2
            ),
            AudioPreset(
                name = "Rock & Metal",
                bands = listOf(6, 4, 2, 0, -2, 2, 5, 6, 5, 4),
                bassBoost = 45,
                loudnessGain = 30,
                spatialSurround = 30,
                reverbPreset = 2
            ),
            AudioPreset(
                name = "Hip-Hop & R&B",
                bands = listOf(8, 7, 5, 2, 0, 1, 3, 4, 5, 5),
                bassBoost = 75,
                loudnessGain = 35,
                spatialSurround = 40,
                reverbPreset = 0
            ),
            AudioPreset(
                name = "Acoustic / Classical",
                bands = listOf(2, 2, 1, 1, 2, 3, 3, 4, 4, 3),
                bassBoost = 15,
                loudnessGain = 10,
                spatialSurround = 45,
                reverbPreset = 3
            ),
            AudioPreset(
                name = "Car Punch",
                bands = listOf(8, 7, 5, 1, 0, 2, 4, 5, 6, 5),
                bassBoost = 70,
                loudnessGain = 50,
                spatialSurround = 60,
                reverbPreset = 0
            )
        )
    }
}
