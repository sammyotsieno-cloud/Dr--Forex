package com.example.ui.screens.research

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.engine.PipelineModuleInfo
import com.example.ui.components.ScientificPrincipleBanner
import com.example.ui.components.StatusBadge
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Indigo700
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700

@Composable
fun ResearchScreen(
    viewModel: ResearchViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("research_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Research Pipeline",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "DISCIPLINED QUANTITATIVE ROADMAP",
                    style = MaterialTheme.typography.labelSmall,
                    color = Indigo600,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        item {
            ScientificPrincipleBanner(
                title = "SYSTEMATIC PHASE PROGRESSION",
                description = "Each module enforces rigorous statistical validation before advancing to downstream stages. Skipping phases or bypassing validation checks violates the Dr. Forex research protocol."
            )
        }

        items(state.modules, key = { it.id }) { module ->
            val isSelected = state.selectedModule?.id == module.id
            ModulePhaseCard(
                module = module,
                isSelected = isSelected,
                onClick = { viewModel.selectModule(module) }
            )
        }

        state.selectedModule?.let { selected ->
            item {
                ModuleDetailInspector(module = selected)
            }
        }
    }
}

@Composable
private fun ModulePhaseCard(
    module: PipelineModuleInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Indigo600 else MaterialTheme.colorScheme.outline
    val bgColor = if (isSelected) Indigo50.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("phase_card_${module.phase}")
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (module.status.isReady) Indigo600.copy(alpha = 0.12f) else Slate100),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P${module.phase}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (module.status.isReady) Indigo600 else Slate400,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            StatusBadge(status = module.status)
        }
    }
}

@Composable
private fun ModuleDetailInspector(
    module: PipelineModuleInfo
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("module_detail_inspector"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PHASE ${module.phase} SPECIFICATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = Indigo600,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
                StatusBadge(status = module.status)
            }

            Text(
                text = module.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = module.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Slate100)
            )

            Text(
                text = "Scientific Constraint & Validation Protocol:",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontSize = 11.sp
            )

            Text(
                text = module.scientificConstraint,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate700,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}
