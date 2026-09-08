// src/components/atoms/TabButton.tsx

import React from 'react';
import { TouchableOpacity, Text, StyleSheet } from 'react-native';

interface TabButtonProps {
  label: string;
  active: boolean;
  onPress: () => void;
}

export function TabButton({ label, active, onPress }: TabButtonProps) {
  return (
    <TouchableOpacity
      style={[styles.tab, active && styles.activeTab]}
      onPress={onPress}
      activeOpacity={0.7}
    >
      <Text style={[styles.label, active && styles.activeLabel]}>{label}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  tab: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 6,
    backgroundColor: 'transparent',
  },
  activeTab: {
    backgroundColor: '#334155',
  },
  label: {
    fontSize: 13,
    color: '#94a3b8',
    fontWeight: '500',
  },
  activeLabel: {
    color: '#e2e8f0',
  },
});
