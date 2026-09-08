// src/components/features/AnalysisTable.tsx

import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { RoundAnalysis, SurfaceClassification } from '../../domain/model/models';
import { Badge } from '../atoms/Badge';

interface AnalysisTableProps {
  analyses: RoundAnalysis[];
  classification: SurfaceClassification;
}

export function AnalysisTable({ analyses, classification }: AnalysisTableProps) {
  if (analyses.length === 0) {
    return (
      <View style={styles.empty}>
        <Text style={styles.emptyText}>No hay análisis disponible. Procesá un patrón primero.</Text>
      </View>
    );
  }

  const classColors: Record<SurfaceClassification, string> = {
    FLAT: '#94a3b8',
    SPHERE: '#4ade80',
    HYPERBOLIC: '#f87171',
    CYLINDRICAL: '#818cf8',
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Análisis de Curvatura</Text>
        <Badge text={classification} color={classColors[classification]} />
      </View>

      <ScrollView horizontal showsHorizontalScrollIndicator={false}>
        <View>
          {/* Header */}
          <View style={styles.tableRow}>
            <Text style={[styles.cell, styles.headerCell, { width: 50 }]}>Fila</Text>
            <Text style={[styles.cell, styles.headerCell, { width: 70 }]}>Puntos</Text>
            <Text style={[styles.cell, styles.headerCell, { width: 70 }]}>ΔN</Text>
            <Text style={[styles.cell, styles.headerCell, { width: 90 }]}>Radio</Text>
            <Text style={[styles.cell, styles.headerCell, { width: 70 }]}>θ°</Text>
            <Text style={[styles.cell, styles.headerCell, { width: 90 }]}>Curvatura</Text>
          </View>

          {/* Rows */}
          {analyses.map((a, i) => {
            const curvatureColor =
              a.localCurvature > 0.1 ? '#4ade80' : a.localCurvature < -0.1 ? '#f87171' : '#94a3b8';

            return (
              <View
                key={i}
                style={[styles.tableRow, i % 2 === 0 && styles.evenRow]}
              >
                <Text style={[styles.cell, { width: 50 }]}>{a.rowIndex}</Text>
                <Text style={[styles.cell, { width: 70 }]}>{a.stitchCount}</Text>
                <Text style={[styles.cell, { width: 70, color: a.deltaN > 0 ? '#4ade80' : a.deltaN < 0 ? '#f87171' : '#94a3b8' }]}>
                  {a.deltaN > 0 ? `+${a.deltaN}` : a.deltaN}
                </Text>
                <Text style={[styles.cell, { width: 90 }]}>{a.theoreticalRadius.toFixed(3)}</Text>
                <Text style={[styles.cell, { width: 70 }]}>{a.inclinationAngleDeg.toFixed(1)}</Text>
                <Text style={[styles.cell, { width: 90, color: curvatureColor }]}>
                  {a.localCurvature >= 0 ? '+' : ''}{a.localCurvature.toFixed(3)}
                </Text>
              </View>
            );
          })}
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: 8,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  title: {
    fontSize: 14,
    fontWeight: '600',
    color: '#e2e8f0',
  },
  tableRow: {
    flexDirection: 'row',
    borderBottomWidth: 1,
    borderBottomColor: '#1e293b',
  },
  evenRow: {
    backgroundColor: '#0f172a',
  },
  cell: {
    paddingVertical: 8,
    paddingHorizontal: 6,
    fontSize: 13,
    fontFamily: 'monospace',
    color: '#cbd5e1',
  },
  headerCell: {
    fontWeight: '600',
    color: '#64748b',
    fontSize: 11,
  },
  empty: {
    padding: 20,
    alignItems: 'center',
  },
  emptyText: {
    color: '#64748b',
    fontSize: 13,
  },
});
