package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.ModuleStatus
import com.example.domain.model.ValidationStatus
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.PolishAmber
import com.example.ui.theme.PolishAmberBg
import com.example.ui.theme.PolishAmberBorder
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishEmeraldBg
import com.example.ui.theme.PolishEmeraldBorder
import com.example.ui.theme.PolishRose
import com.example.ui.theme.PolishRoseBg
import com.example.ui.theme.PolishRoseBorder
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600

@Composable
fun StatusBadge(
    status: ModuleStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor, dotColor) = if (status.isReady) {
        Quad(
            Indigo50,
            Indigo600,
            Indigo600.copy(alpha = 0.2f),
            Indigo600
        )
    } else {
        Quad(
            Slate100,
            Slate400,
            Slate200,
            Slate400
        )
    }

    Row(
        modifier = modifier
            .testTag("status_badge_${status.name}")
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ValidationStatusBadge(
    status: ValidationStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor, label) = when (status) {
        ValidationStatus.VALID -> Quad(PolishEmeraldBg, PolishEmerald, PolishEmeraldBorder, "VALIDATED")
        ValidationStatus.WARNINGS_FOUND -> Quad(PolishAmberBg, PolishAmber, PolishAmberBorder, "WARNINGS")
        ValidationStatus.INVALID -> Quad(PolishRoseBg, PolishRose, PolishRoseBorder, "INVALID")
        ValidationStatus.UNCHECKED -> Quad(Indigo50, Indigo600, Indigo600.copy(alpha = 0.2f), "UNCHECKED")
    }

    Row(
        modifier = modifier
            .testTag("validation_status_badge_${status.name}")
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(textColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
