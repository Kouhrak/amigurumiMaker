// src/__tests__/components/FeatureScreens.test.tsx

import React from 'react';
import { render } from '@testing-library/react-native';
import { AnalysisTable } from '../../components/features/AnalysisTable';
import { StitchInfo } from '../../components/features/StitchInfo';
import { makeParsedRow } from '../testHelpers';
import { StitchType } from '../../domain/model/types';
import { RoundAnalysis } from '../../domain/model/models';

function makeAnalysis(overrides: Partial<RoundAnalysis> = {}): RoundAnalysis {
  return {
    rowIndex: 1,
    stitchCount: 6,
    deltaN: 0,
    theoreticalRadius: 0.4775,
    cosTheta: 1.0,
    sinTheta: 0.0,
    inclinationAngleDeg: 0.0,
    liftZ: 0.0,
    localCurvature: 0.0,
    roundArea: 1.5,
    ...overrides,
  };
}

describe('AnalysisTable', () => {
  it('shows empty message when no analyses', () => {
    const { getByText } = render(
      <AnalysisTable analyses={[]} classification="FLAT" />
    );
    getByText('No hay análisis disponible. Procesá un patrón primero.');
  });

  it('renders table rows', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, deltaN: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 12, deltaN: 6 }),
    ];
    const { getByText } = render(
      <AnalysisTable analyses={analyses} classification="SPHERE" />
    );
    getByText('Análisis de Curvatura');
    getByText('SPHERE');
  });
});

describe('StitchInfo', () => {
  it('renders all stitch types', () => {
    const counts: Record<StitchType, number> = {
      [StitchType.NORMAL]: 10,
      [StitchType.INCREASE]: 5,
      [StitchType.DECREASE]: 3,
      [StitchType.CHAIN]: 8,
    };
    const { getByText } = render(
      <StitchInfo stitchCounts={counts} total={26} />
    );
    getByText('Referencia de Puntos');
    getByText('Punto Normal');
    getByText('Aumento (v)');
    getByText('Disminución (a)');
    getByText('Cadena (c)');
  });

  it('shows zero counts', () => {
    const counts: Record<StitchType, number> = {
      [StitchType.NORMAL]: 0,
      [StitchType.INCREASE]: 0,
      [StitchType.DECREASE]: 0,
      [StitchType.CHAIN]: 0,
    };
    const { getAllByText } = render(
      <StitchInfo stitchCounts={counts} total={0} />
    );
    const zeroBadges = getAllByText('0 (0%)');
    expect(zeroBadges.length).toBe(4);
  });
});
