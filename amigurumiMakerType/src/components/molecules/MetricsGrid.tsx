// src/components/molecules/MetricsGrid.tsx

import React from 'react';
import { View, StyleSheet } from 'react-native';
import { MetricCard } from '../atoms/MetricCard';
import { SurfaceMetrics, SurfaceClassification } from '../../domain/model/models';

interface MetricsGridProps {
  metrics: SurfaceMetrics | null;
  classification: SurfaceClassification;
}

export function MetricsGrid({ metrics, classification }: MetricsGridProps) {
  if (!metrics) return null;

  const classColors: Record<SurfaceClassification, string> = {
    FLAT: '#94a3b8',
    SPHERE: '#4ade80',
    HYPERBOLIC: '#f87171',
    CYLINDRICAL: '#818cf8',
  };

  return (
    <View style={styles.grid}>
      <MetricCard
        label="Área total"
        value={metrics.totalArea.toFixed(2)}
        unit="cm²"
      />
      <MetricCard
        label="Curvatura total"
        value={metrics.totalCurvature.toFixed(4)}
      />
      <MetricCard
        label="Car. Euler"
        value={metrics.eulerCharacteristic.toFixed(4)}
      />
      <MetricCard
        label="Clasificación"
        value={classification}
        color={classColors[classification]}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
});
