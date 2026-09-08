// src/engine/AmigurumiCanvas.tsx
// React Three Fiber canvas with orbit controls, lighting, and mesh rendering

import React, { useRef, useEffect, useMemo } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { OrbitControls } from '@react-three/drei';
import * as THREE from 'three';
import { RoundAnalysis, MeshSettings } from '../domain/model/models';
import { useAmigurumiMesh } from './useAmigurumiMesh';
import {
  DEFAULT_CAMERA_POSITION,
  DEFAULT_FOV,
  AMBIENT_LIGHT_COLOR,
  AMBIENT_LIGHT_INTENSITY,
  DIRECTIONAL_LIGHT_1,
  DIRECTIONAL_LIGHT_2,
} from '../domain/constants';

// --- Inner mesh component (handles geometry + material) ---

interface MeshObjectProps {
  analyses: RoundAnalysis[];
  settings: MeshSettings;
}

function MeshObject({ analyses, settings }: MeshObjectProps) {
  const meshRef = useRef<THREE.Mesh>(null);
  const meshData = useAmigurumiMesh(analyses, settings);

  // Dispose geometry on unmount or when meshData changes
  useEffect(() => {
    return () => {
      if (meshRef.current?.geometry) {
        meshRef.current.geometry.dispose();
      }
    };
  }, [meshData]);

  if (!meshData) return null;

  return (
    <mesh ref={meshRef} geometry={meshData.geometry}>
      <meshStandardMaterial
        vertexColors
        side={THREE.DoubleSide}
        wireframe={settings.wireframe}
      />
    </mesh>
  );
}

// --- Main canvas component ---

export interface AmigurumiCanvasProps {
  analyses: RoundAnalysis[];
  settings: MeshSettings;
  style?: React.ViewStyle;
}

export function AmigurumiCanvas({ analyses, settings, style }: AmigurumiCanvasProps) {
  return (
    <Canvas
      camera={{ position: DEFAULT_CAMERA_POSITION, fov: DEFAULT_FOV }}
      style={[{ flex: 1 }, style]}
      gl={{ antialias: true }}
    >
      {/* Lighting */}
      <ambientLight
        intensity={AMBIENT_LIGHT_INTENSITY}
        color={AMBIENT_LIGHT_COLOR}
      />
      <directionalLight
        position={DIRECTIONAL_LIGHT_1.position}
        intensity={DIRECTIONAL_LIGHT_1.intensity}
        color={DIRECTIONAL_LIGHT_1.color}
      />
      <directionalLight
        position={DIRECTIONAL_LIGHT_2.position}
        intensity={DIRECTIONAL_LIGHT_2.intensity}
        color={DIRECTIONAL_LIGHT_2.color}
      />

      {/* 3D Mesh */}
      <MeshObject analyses={analyses} settings={settings} />

      {/* Controls */}
      <OrbitControls makeDefault />
    </Canvas>
  );
}
