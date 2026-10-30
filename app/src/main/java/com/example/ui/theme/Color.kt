package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// "لسه فاكرني؟" Design Tokens — Brand Palette
// ==========================================

// Primary Background & Surface Hierarchy
val BgMain = Color(0xFF050816)              // Deep obsidian navy
val BgSecondary = Color(0xFF080D20)         // Atmospheric secondary navy
val SurfaceElevated = Color(0xFF0D1530)     // Elevated dark navy surface
val SurfaceCard = Color(0xFF101A35)         // Structured card base
val SurfaceDeepGlass = Color(0xFF11172B)    // Translucent glass base

// Translucent Glass Surfaces
val GlassFill = Color(0xCC101A35)           // 80% opacity dark navy card glass
val GlassFillSubtle = Color(0x800D1530)     // 50% opacity background glass
val GlassBorder = Color(0x2E00E5FF)         // Faint electric cyan glass border
val GlassBorderSoft = Color(0x26C4B5FD)     // Faint lavender edge highlight

// Neon Accent Tokens (Inspired by the App Icon)
val NeonCyan = Color(0xFF00E5FF)            // Electric cyan (time, action, active state)
val NeonBlue = Color(0xFF287BFF)            // Neon blue (structure, secondary action)
val NeonViolet = Color(0xFF8B5CF6)          // Electric violet (orbital rings, graduation cap glow)
val NeonMagenta = Color(0xFFFF45D4)         // Neon magenta (highlight border, alert)
val SoftLavender = Color(0xFFC4B5FD)        // Luminous soft lavender for badges & details

// Semantic Status Colors
val StatusSuccess = Color(0xFF35D6A0)       // Mint green (attendance present, completed)
val StatusWarning = Color(0xFFFFCA70)       // Warm amber (exam approaching, sponsored ad)
val StatusError = Color(0xFFFF647C)         // Coral red (missed lecture, overdue, delete)

// Text & Typography Tokens
val TextPrimary = Color(0xFFF8FAFF)         // Crisp luminous off-white
val TextSecondary = Color(0xFFA7B2D0)       // Cool readable slate
val TextMuted = Color(0xFF6F7C9D)           // Soft low-emphasis secondary
val TextOnNeon = Color(0xFF050816)          // Inverted text on vibrant neon buttons

// Backward compatibility aliases if referenced
val DarkBackground = BgMain
val DarkSurface = SurfaceCard
val GlassSurface = GlassFill
val EmeraldNeon = StatusSuccess
val AmethystViolet = NeonViolet
val CobaltBlue = NeonBlue
