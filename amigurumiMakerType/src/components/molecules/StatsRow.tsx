// src/components/molecules/StatsRow.tsx

import React from 'react';
import { View, StyleSheet } from 'react-native';
import { StatCard } from '../atoms/StatCard';

interface StatsRowProps {
  totalStitches: number;
  totalIncreases: number;
  totalDecreases: number;
}

export function StatsRow({ totalStitches, totalIncreases, totalDecreases }: StatsRowProps) {
  return (
    <View style={styles.row}>
      <StatCard label="Puntos" value={totalStitches} icon="🧶" color="#e2e8f0" />
      <StatCard label="Aumentos" value={totalIncreases} icon="▲" color="#4ade80" />
      <StatCard label="Disminuciones" value={totalDecreases} icon="▼" color="#f87171" />
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    gap: 8,
    justifyContent: 'space-between',
  },
});
