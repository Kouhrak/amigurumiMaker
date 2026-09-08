// src/__tests__/domain/models.test.ts
// Smoke tests for data models

import {
  ParsedRow,
  ParsedToken,
  RoundAnalysis,
  SurfaceMetrics,
  MeshSettings,
  Float3,
  RingSegment,
  RevolutionMesh,
  BlockGeometry,
  MeshCell,
  CylinderCell,
  PresetPattern,
} from '../../domain/model/models';
import { StitchType, ColorMode } from '../../domain/model/types';

describe('MeshSettings defaults', () => {
  it('can be constructed with defaults', () => {
    const settings: MeshSettings = {
      stitchWidth: 0.5,
      stitchHeight: 0.5,
      wireframe: false,
      colorMode: 'GAUSS_HEATMAP',
    };
    expect(settings.stitchWidth).toBe(0.5);
    expect(settings.stitchHeight).toBe(0.5);
    expect(settings.wireframe).toBe(false);
    expect(settings.colorMode).toBe('GAUSS_HEATMAP');
  });
});

describe('Float3', () => {
  it('constructs with x, y, z', () => {
    const v: Float3 = { x: 1.0, y: 2.0, z: 3.0 };
    expect(v.x).toBe(1.0);
    expect(v.y).toBe(2.0);
    expect(v.z).toBe(3.0);
  });
});

describe('ParsedRow', () => {
  it('constructs with all fields', () => {
    const token: ParsedToken = {
      type: StitchType.NORMAL,
      count: 3,
      yieldCount: 3,
    };
    const row: ParsedRow = {
      raw: '1) 3p (3p)',
      rowIndex: 1,
      tokens: [token],
      totalCalculated: 3,
      totalExpected: 3,
      isValid: true,
      errorMsg: '',
      increaseCount: 0,
      decreaseCount: 0,
    };
    expect(row.totalCalculated).toBe(3);
    expect(row.isValid).toBe(true);
  });
});

describe('RoundAnalysis', () => {
  it('constructs with all fields', () => {
    const analysis: RoundAnalysis = {
      rowIndex: 1,
      stitchCount: 6,
      deltaN: 0,
      theoreticalRadius: 0.477,
      cosTheta: 1.0,
      sinTheta: 0.0,
      inclinationAngleDeg: 0.0,
      liftZ: 0.0,
      localCurvature: 0.0,
      roundArea: 1.5,
    };
    expect(analysis.stitchCount).toBe(6);
    expect(analysis.theoreticalRadius).toBeCloseTo(0.477, 2);
  });
});

describe('RevolutionMesh', () => {
  it('constructs with segments and totalHeight', () => {
    const segment: RingSegment = {
      vertices: [{ x: 0, y: 0, z: 0 }],
      indices: [0],
      color: { x: 0.06, y: 0.72, z: 0.50 },
    };
    const mesh: RevolutionMesh = {
      segments: [segment],
      totalHeight: 2.5,
    };
    expect(mesh.segments).toHaveLength(1);
    expect(mesh.totalHeight).toBe(2.5);
  });
});

describe('PresetPattern', () => {
  it('constructs with name, description, pattern', () => {
    const preset: PresetPattern = {
      name: 'Esfera',
      description: 'K > 0 — Curvatura esférica positiva',
      pattern: '1) 6c (6p)\n2) [1a] 6v (12p)',
    };
    expect(preset.name).toBe('Esfera');
    expect(preset.pattern).toContain('\n');
  });
});
