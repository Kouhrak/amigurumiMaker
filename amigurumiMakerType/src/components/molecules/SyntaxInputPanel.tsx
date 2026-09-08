// src/components/molecules/SyntaxInputPanel.tsx

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Input } from '../atoms/Input';
import { Button } from '../atoms/Button';

interface SyntaxInputPanelProps {
  value: string;
  onChangeText: (text: string) => void;
  onParse: () => void;
  isLoading: boolean;
}

export function SyntaxInputPanel({ value, onChangeText, onParse, isLoading }: SyntaxInputPanelProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.label}>Patrón de crochet</Text>
      <Input
        value={value}
        onChangeText={onChangeText}
        placeholder="1) 6c (6p)&#10;2) [1a] 6v (12p)"
        multiline
        style={styles.input}
      />
      <Button
        title={isLoading ? 'Procesando...' : 'Procesar'}
        onPress={onParse}
        disabled={isLoading || value.trim().length === 0}
        loading={isLoading}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: 8,
  },
  label: {
    fontSize: 12,
    color: '#94a3b8',
    fontWeight: '500',
  },
  input: {
    minHeight: 80,
  },
});
