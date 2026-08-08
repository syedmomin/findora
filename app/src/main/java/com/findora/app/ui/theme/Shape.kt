package com.findora.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Radius scale from the spec: buttons 16, cards 20, search 24, bottom sheet 28.
val FindoraShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),   // buttons
    medium = RoundedCornerShape(20.dp),  // cards
    large = RoundedCornerShape(24.dp),   // search bar
    extraLarge = RoundedCornerShape(28.dp), // bottom sheet
)

// Named aliases so call sites read intent, not size.
object FindoraRadius {
    val Button = RoundedCornerShape(16.dp)
    val Card = RoundedCornerShape(20.dp)
    val Search = RoundedCornerShape(24.dp)
    val Sheet = RoundedCornerShape(28.dp)
}
