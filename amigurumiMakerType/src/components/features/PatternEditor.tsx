// src/components/features/PatternEditor.tsx

import React from 'react';
import { View, StyleSheet } from 'react-native';
import { SyntaxInputPanel } from '../molecules/SyntaxInputPanel';
import { PresetsBar } from '../molecules/PresetsBar';
import { StatsRow } from '../molecules/StatsRow';
import { useAmigurumiStore } from '../../store/useAmigurumiStore';

export function PatternEditor() {
  const syntaxText = useAmigurumiStore((s) => s.syntaxText);
  const isLoading = useAmigurumiStore((s) => s.isLoading);
  const totalStitches = useAmigurumiStore((s) => s.totalStitches);
  const totalIncreases = useAmigurumiStore((s) => s.totalIncreases);
  const totalDecreases = useAmigurumiStore((s) => s.totalDecreases);
  const parseSyntax = useAmigurumiStore((s) => s.parseSyntax);
  const loadPreset = useAmigurumiStore((s) => s.loadPreset);

  return (
    <View style={styles.container}>
      <SyntaxInputPanel
        value={syntaxText}
        onChangeText={parseSyntax}
        onParse={() => parseSyntax(syntaxText)}
        isLoading={isLoading}
      />
      <PresetsBar onSelect={loadPreset} />
      {totalStitches > 0 && (
        <StatsRow
          totalStitches={totalStitches}
          totalIncreases={totalIncreases}
          totalDecreases={totalDecreases}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: 12,
  },
});
