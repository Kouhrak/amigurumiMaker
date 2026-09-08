// src/components/atoms/Badge.tsx

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

interface BadgeProps {
  text: string;
  color: string;
  bgColor?: string;
}

export function Badge({ text, color, bgColor }: BadgeProps) {
  return (
    <View style={[styles.badge, { backgroundColor: bgColor || `${color}20` }]}>
      <Text style={[styles.text, { color }]}>{text}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  badge: {
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 12,
    alignSelf: 'flex-start',
  },
  text: {
    fontSize: 11,
    fontWeight: '600',
  },
});
