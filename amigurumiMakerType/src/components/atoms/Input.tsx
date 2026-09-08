// src/components/atoms/Input.tsx

import React from 'react';
import { TextInput, StyleSheet, TextInputProps } from 'react-native';

interface InputProps extends TextInputProps {
  value: string;
  onChangeText: (text: string) => void;
  placeholder?: string;
  multiline?: boolean;
}

export function Input({ value, onChangeText, placeholder, multiline, ...rest }: InputProps) {
  return (
    <TextInput
      style={[styles.input, multiline && styles.multiline]}
      value={value}
      onChangeText={onChangeText}
      placeholder={placeholder}
      placeholderTextColor="#64748b"
      multiline={multiline}
      autoCapitalize="none"
      autoCorrect={false}
      {...rest}
    />
  );
}

const styles = StyleSheet.create({
  input: {
    backgroundColor: '#1e293b',
    borderWidth: 1,
    borderColor: '#334155',
    borderRadius: 8,
    padding: 12,
    color: '#e2e8f0',
    fontSize: 14,
    fontFamily: 'monospace',
    minHeight: 44,
  },
  multiline: {
    minHeight: 100,
    textAlignVertical: 'top',
  },
});
