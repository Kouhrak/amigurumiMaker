// src/components/features/StitchInfo.tsx

import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { StitchType } from '../../domain/model/types';
import { StitchSymbol } from '../atoms/StitchSymbol';
import { Badge } from '../atoms/Badge';

interface StitchInfoProps {
  stitchCounts: Record<StitchType, number>;
  total: number;
}

const STITCH_DATA: Record<StitchType, { name: string; description: string; symbol: string; color: string }> = {
  [StitchType.NORMAL]: {
    name: 'Punto Normal',
    description: 'Punto básico de crochet. Sin modificación de cantidad.',
    symbol: '●',
    color: '#e2e8f0',
  },
  [StitchType.INCREASE]: {
    name: 'Aumento (v)',
    description: 'Insertar 2 puntos en 1 punto base. Aumenta el número de puntos.',
    symbol: '▲',
    color: '#4ade80',
  },
  [StitchType.DECREASE]: {
    name: 'Disminución (a)',
    description: 'Juntar 2 puntos base en 1 punto. Reduce el número de puntos.',
    symbol: '▼',
    color: '#f87171',
  },
  [StitchType.CHAIN]: {
    name: 'Cadena (c)',
    description: 'Punto de cadena. Usado para crear la base o para subir de nivel.',
    symbol: '○',
    color: '#94a3b8',
  },
};

export function StitchInfo({ stitchCounts, total }: StitchInfoProps) {
  const types = [StitchType.CHAIN, StitchType.NORMAL, StitchType.INCREASE, StitchType.DECREASE];

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Referencia de Puntos</Text>

      {types.map((type) => {
        const data = STITCH_DATA[type];
        const count = stitchCounts[type] || 0;
        const pct = total > 0 ? ((count / total) * 100).toFixed(0) : '0';

        return (
          <View key={type} style={styles.card}>
            <View style={styles.cardHeader}>
              <StitchSymbol type={type} size={20} />
              <View style={styles.cardTitle}>
                <Text style={styles.stitchName}>{data.name}</Text>
                <Badge text={`${count} (${pct}%)`} color={data.color} />
              </View>
            </View>
            <Text style={styles.description}>{data.description}</Text>
          </View>
        );
      })}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: 8,
  },
  title: {
    fontSize: 14,
    fontWeight: '600',
    color: '#e2e8f0',
    marginBottom: 4,
  },
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 8,
    padding: 12,
    marginBottom: 8,
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  cardTitle: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  stitchName: {
    fontSize: 14,
    fontWeight: '600',
    color: '#e2e8f0',
  },
  description: {
    fontSize: 12,
    color: '#94a3b8',
    marginTop: 6,
    lineHeight: 16,
  },
});
