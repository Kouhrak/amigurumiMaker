// src/app/(tabs)/index.tsx

import React, { useState } from 'react';
import { View, ScrollView, StyleSheet, Platform } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { HeaderBar } from '../../components/molecules/HeaderBar';
import { PatternEditor } from '../../components/features/PatternEditor';
import { AnalysisTable } from '../../components/features/AnalysisTable';
import { StitchInfo } from '../../components/features/StitchInfo';
import { MetricsGrid } from '../../components/molecules/MetricsGrid';
import { AmigurumiCanvas } from '../../engine/AmigurumiCanvas';
import { useAmigurumiStore } from '../../store/useAmigurumiStore';
import { StitchType } from '../../domain/model/types';
import { InfoTab } from '../../domain/model/models';

export default function EditorScreen() {
  const activeTab = useAmigurumiStore((s) => s.activeTab);
  const setActiveTab = useAmigurumiStore((s) => s.setActiveTab);
  const parsedRows = useAmigurumiStore((s) => s.parsedRows);
  const roundAnalyses = useAmigurumiStore((s) => s.roundAnalyses);
  const surfaceMetrics = useAmigurumiStore((s) => s.surfaceMetrics);
  const surfaceClassification = useAmigurumiStore((s) => s.surfaceClassification);
  const revolutionMesh = useAmigurumiStore((s) => s.revolutionMesh);
  const wireframeEnabled = useAmigurumiStore((s) => s.wireframeEnabled);
  const totalStitches = useAmigurumiStore((s) => s.totalStitches);

  // Compute stitch counts
  const stitchCounts: Record<StitchType, number> = {
    [StitchType.NORMAL]: 0,
    [StitchType.INCREASE]: 0,
    [StitchType.DECREASE]: 0,
    [StitchType.CHAIN]: 0,
  };

  parsedRows.forEach((row) => {
    row.parsedStitches.forEach((s) => {
      stitchCounts[s.type] = (stitchCounts[s.type] || 0) + s.count;
    });
  });

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView style={styles.container} contentContainerStyle={styles.content}>
        <HeaderBar activeTab={activeTab} onTabChange={setActiveTab} />

        {/* 3D Preview — always visible if we have a mesh */}
        {revolutionMesh && revolutionMesh.segments.length > 0 && (
          <View style={styles.canvasContainer}>
            <AmigurumiCanvas
              revolutionMesh={revolutionMesh}
              wireframe={wireframeEnabled}
            />
          </View>
        )}

        {/* Tab content */}
        {activeTab === 'CATALOG' ? (
          <View style={styles.tabContent}>
            <PatternEditor />
            <MetricsGrid metrics={surfaceMetrics} classification={surfaceClassification} />
            <AnalysisTable analyses={roundAnalyses} classification={surfaceClassification} />
          </View>
        ) : (
          <View style={styles.tabContent}>
            <StitchInfo stitchCounts={stitchCounts} total={totalStitches} />
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#0f172a',
  },
  container: {
    flex: 1,
  },
  content: {
    padding: 16,
    paddingBottom: 40,
  },
  canvasContainer: {
    height: 300,
    borderRadius: 12,
    overflow: 'hidden',
    marginBottom: 16,
    backgroundColor: '#1a1a2e',
  },
  tabContent: {
    gap: 16,
  },
});
