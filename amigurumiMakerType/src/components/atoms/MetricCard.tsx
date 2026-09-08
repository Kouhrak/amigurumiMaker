// src/components/atoms/MetricCard.tsx

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

interface MetricCardProps {
  label: string;
  value: string;
  unit?: string;
  color?: string;
}

export function MetricCard({ label, value, unit, color = '#e2e8f0' }: MetricCardProps) {
  return (
    <View style={styles.card}>
      <Text style={styles.label}>{label}</Text>
      <View style={styles.valueRow}>
        <Text style={[styles.value, { color }]}>{value}</Text>
        {unit && <Text style={styles.unit}>{unit}</Text>}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 8,
    padding: 10,
  },
  label: {
    fontSize: 11,
    color: '#94a3b8',
    marginBottom: 4,
  },
  valueRow: {
    flexDirection: 'row',
    alignItems: 'baseline',
  },
  value: {
    fontSize: 16,
    fontWeight: 'bold',
    fontFamily: 'monospace',
  },
  unit: {
    fontSize: 12,
    color: '#64748b',
    marginLeft: 4,
  },
});
