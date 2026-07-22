package com.example.amigurumimaker.presentation.wallmodeler.ui.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.amigurumimaker.domain.model.InfoTab
import com.example.amigurumimaker.presentation.wallmodeler.ui.atoms.TabButton
import com.example.amigurumimaker.presentation.wallmodeler.ui.molecules.GeometricRules
import com.example.amigurumimaker.presentation.wallmodeler.ui.molecules.StitchCatalog

@Composable
fun StitchInfoPanel(
    activeTab: InfoTab,
    onTabChange: (InfoTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TabButton(
                label = "Catálogo Puntos",
                isSelected = activeTab == InfoTab.CATALOG,
                onClick = { onTabChange(InfoTab.CATALOG) }
            )
            TabButton(
                label = "Reglas Geométricas",
                isSelected = activeTab == InfoTab.RULES,
                onClick = { onTabChange(InfoTab.RULES) }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        when (activeTab) {
            InfoTab.CATALOG -> StitchCatalog()
            InfoTab.RULES -> GeometricRules()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StitchInfoPanelCatalogPreview() {
    StitchInfoPanel(activeTab = InfoTab.CATALOG, onTabChange = {})
}

@Preview(showBackground = true)
@Composable
private fun StitchInfoPanelRulesPreview() {
    StitchInfoPanel(activeTab = InfoTab.RULES, onTabChange = {})
}
