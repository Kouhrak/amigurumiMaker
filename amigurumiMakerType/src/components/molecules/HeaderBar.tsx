// src/components/molecules/HeaderBar.tsx

import React from 'react';
import { View, StyleSheet } from 'react-native';
import { HeaderTitle } from '../atoms/HeaderTitle';
import { TabButton } from '../atoms/TabButton';
import { InfoTab } from '../../domain/model/models';

interface HeaderBarProps {
  activeTab: InfoTab;
  onTabChange: (tab: InfoTab) => void;
}

export function HeaderBar({ activeTab, onTabChange }: HeaderBarProps) {
  return (
    <View style={styles.container}>
      <HeaderTitle title="Amigurumi Maker" subtitle="React Native Multiplatform" />
      <View style={styles.tabs}>
        <TabButton
          label="Catálogo"
          active={activeTab === 'CATALOG'}
          onPress={() => onTabChange('CATALOG')}
        />
        <TabButton
          label="Reglas"
          active={activeTab === 'RULES'}
          onPress={() => onTabChange('RULES')}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
  },
  tabs: {
    flexDirection: 'row',
    gap: 4,
  },
});
