package com.example.data.model

enum class DeviceType(val displayName: String, val id: String) {
    HEADPHONES("Headphones", "headphones"),
    EARBUDS("Earbuds", "earbuds"),
    CAR_STEREO("Car Stereo", "car_stereo"),
    SPEAKERS("Speakers", "speakers"),
    CUSTOM("Custom", "custom");

    companion object {
        fun fromId(id: String): DeviceType {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: HEADPHONES
        }
    }
}
