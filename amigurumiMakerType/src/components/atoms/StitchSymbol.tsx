// src/components/atoms/StitchSymbol.tsx

import React from 'react';
import { Text, StyleSheet } from 'react-native';
import { StitchType } from '../../domain/model/types';

interface StitchSymbolProps {
  type: StitchType;
  size?: number;
}

const SYMBOL_MAP: Record<StitchType, string> = {
  [StitchType.NORMAL]: '●',
  [StitchType.INCREASE]: '▲',
  [StitchType.DECREASE]: '▼',
  [StitchType.CHAIN]: '○',
};

const COLOR_MAP: Record<StitchType, string> = {
  [StitchType.NORMAL]: '#e2e8f0',
  [StitchType.INCREASE]: '#4ade80',
  [StitchType.DECREASE]: '#f87171',
  [StitchType.CHAIN]: '#94a3b8',
};

export function StitchSymbol({ type, size = 14 }: StitchSymbolProps) {
  return (
    <Text style={[styles.symbol, { fontSize: size, color: COLOR_MAP[type] }]}>
      {SYMBOL_MAP[type]}
    </Text>
  );
}

const styles = StyleSheet.create({
  symbol: {
    fontFamily: 'monospace',
    textAlign: 'center',
  },
});
