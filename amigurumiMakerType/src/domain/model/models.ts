// src/domain/model/models.ts
// Data models — direct translation from Kotlin data classes

import { StitchType, ColorMode, ViewMode, InfoTab, SurfaceClassification } from './types';

// --- Token & Row models ---

export interface ParsedToken {
  type: StitchType;
  count: number;
  yieldCount: number;
}

export interface ParsedRow {
  raw: string;
  rowIndex: number;
  tokens: ParsedToken[];
  totalCalculated: number;
  totalExpected: number | null;
  isValid: boolean;
  errorMsg: string;
  increaseCount: number;
  decreaseCount: number;
}

// --- Geometry models ---

export interface Float3 {
  x: number;
  y: number;
  z: number;
}

export interface RingSegment {
  vertices: Float3[];
  indices: number[];
  color: Float3;
}

export interface RevolutionMesh {
  segments: RingSegment[];
  totalHeight: number;
}

export interface BlockGeometry {
  vertices: Float3[];
  edges: [number, number][];
  faceIndices: [number, number, number][];
}

// --- 2D mesh models (for MESH_2D view) ---

export interface MeshCell {
  row: number;
  col: number;
  type: string;
  x: number;
  y: number;
}

export interface CylinderCell {
  row: number;
  col: number;
  type: string;
  angle: number;
  y: number;
  radius: number;
}

// --- Analysis models ---

export interface RoundAnalysis {
  rowIndex: number;
  stitchCount: number;
  deltaN: number;
  theoreticalRadius: number;
  cosTheta: number;
  sinTheta: number;
  inclinationAngleDeg: number;
  liftZ: number;
  localCurvature: number;
  roundArea: number;
}

export interface SurfaceMetrics {
  totalArea: number;
  totalCurvature: number;
  eulerCharacteristic: number;
}

// --- Settings ---

export interface MeshSettings {
  stitchWidth: number;
  stitchHeight: number;
  wireframe: boolean;
  colorMode: ColorMode;
}

// --- Presets ---

export interface PresetPattern {
  name: string;
  description: string;
  pattern: string;
}
