// src/components/molecules/PresetsBar.tsx

import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { PRESETS } from '../../domain/PresetPatterns';

interface PresetsBarProps {
  onSelect: (index: number) => void;
}

export function PresetsBar({ onSelect }: PresetsBarProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.label}>Presets</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.scroll}>
        {PRESETS.map((preset, i) => (
          <TouchableOpacity
            key={preset.name}
            style={styles.preset}
            onPress={() => onSelect(i)}
            activeOpacity={0.7}
          >
            <Text style={styles.name}>{preset.name}</Text>
            <Text style={styles.desc} numberOfLines={1}>{preset.description}</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: 6,
  },
  label: {
    fontSize: 12,
    color: '#94a3b8',
    fontWeight: '500',
  },
  scroll: {
    flexGrow: 0,
  },
  preset: {
    backgroundColor: '#1e293b',
    borderWidth: 1,
    borderColor: '#334155',
    borderRadius: 8,
    padding: 10,
    marginRight: 8,
    minWidth: 120,
  },
  name: {
    fontSize: 13,
    fontWeight: '600',
    color: '#e2e8f0',
  },
  desc: {
    fontSize: 11,
    color: '#64748b',
    marginTop: 2,
  },
});
