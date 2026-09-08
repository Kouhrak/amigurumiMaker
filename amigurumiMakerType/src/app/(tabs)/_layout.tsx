// src/app/(tabs)/_layout.tsx
// Tab layout — single tab for now (main editor)

import { Tabs } from 'expo-router';
import { Text } from 'react-native';

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: '#818cf8',
        tabBarInactiveTintColor: '#94a3b8',
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: 'Amigurumi Maker',
          tabBarIcon: ({ color }) => (
            <Text style={{ fontSize: 20, color }}>🧶</Text>
          ),
        }}
      />
    </Tabs>
  );
}
