// src/components/atoms/StatCard.tsx

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

interface StatCardProps {
  label: string;
  value: string | number;
  icon?: string;
  color?: string;
}

export function StatCard({ label, value, icon, color = '#e2e8f0' }: StatCardProps) {
  return (
    <View style={styles.card}>
      {icon && <Text style={styles.icon}>{icon}</Text>}
      <Text style={[styles.value, { color }]}>{value}</Text>
      <Text style={styles.label}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 8,
    padding: 12,
    alignItems: 'center',
    minWidth: 80,
  },
  icon: {
    fontSize: 16,
    marginBottom: 4,
  },
  value: {
    fontSize: 20,
    fontWeight: 'bold',
    fontFamily: 'monospace',
  },
  label: {
    fontSize: 11,
    color: '#94a3b8',
    marginTop: 2,
  },
});
