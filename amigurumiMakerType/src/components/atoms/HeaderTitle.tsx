// src/components/atoms/HeaderTitle.tsx

import React from 'react';
import { Text, StyleSheet } from 'react-native';

interface HeaderTitleProps {
  title: string;
  subtitle?: string;
}

export function HeaderTitle({ title, subtitle }: HeaderTitleProps) {
  return (
    <>
      <Text style={styles.title}>{title}</Text>
      {subtitle && <Text style={styles.subtitle}>{subtitle}</Text>}
    </>
  );
}

const styles = StyleSheet.create({
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#e2e8f0',
  },
  subtitle: {
    fontSize: 12,
    color: '#818cf8',
    marginTop: 2,
  },
});
