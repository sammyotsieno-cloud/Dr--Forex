package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_config")
data class VoiceConfigEntity(
    @PrimaryKey val id: Int = 1,
    val mode: String = "SYSTEM_TTS", // "CUSTOM_VOICE" or "SYSTEM_TTS"
    val apiKey: String = "",
    val voiceId: String = "default",
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val explanationTier: String = "ELI3", // "ELI3", "TRADER", "QUANT"
    val autoPlayVoice: Boolean = true
)
