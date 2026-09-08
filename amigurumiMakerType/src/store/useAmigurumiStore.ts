// src/store/useAmigurumiStore.ts
// Zustand store — port of Kotlin WallModelerViewModel + WallModelerState

import { create } from 'zustand';
import {
  ParsedRow,
  RoundAnalysis,
  SurfaceMetrics,
  RevolutionMesh,
  MeshCell,
  CylinderCell,
  MeshSettings,
  ColorMode,
  ViewMode,
  InfoTab,
  SurfaceClassification,
} from '../domain/model/models';
import { parse } from '../domain/PatternParser';
import { computeRoundAnalyses, computeSurfaceMetrics, classifySurface } from '../domain/CurvatureComputer';
import { computeMesh } from '../domain/RevolutionMeshComputer';
import { compute2DCells, compute3DProjection } from '../domain/MeshComputer';
import { PRESETS } from '../domain/PresetPatterns';
import { MIN_SCALE, MAX_SCALE } from '../domain/constants';

export interface AmigurumiState {
  // Data
  syntaxText: string;
  parsedRows: ParsedRow[];
  roundAnalyses: RoundAnalysis[];
  revolutionMesh: RevolutionMesh | null;
  meshCells: MeshCell[];
  cylinderCells: CylinderCell[];
  surfaceMetrics: SurfaceMetrics | null;
  surfaceClassification: SurfaceClassification;

  // UI
  isLoading: boolean;
  error: string | null;
  viewMode: ViewMode;
  activeTab: InfoTab;
  colorMode: ColorMode;
  wireframeEnabled: boolean;

  // Viewport
  scale: number;
  offsetX: number;
  offsetY: number;

  // Computed
  totalStitches: number;
  totalIncreases: number;
  totalDecreases: number;
  logMessage: string;

  // Actions
  parseSyntax: (text: string) => void;
  reset: () => void;
  zoomBy: (factor: number) => void;
  resetView: () => void;
  setViewMode: (mode: ViewMode) => void;
  setActiveTab: (tab: InfoTab) => void;
  loadExample: (index: number) => void;
  dragBy: (dx: number, dy: number) => void;
  setColorMode: (mode: ColorMode) => void;
  setWireframe: (enabled: boolean) => void;
  loadPreset: (index: number) => void;
}

const INITIAL_STATE = {
  syntaxText: '',
  parsedRows: [] as ParsedRow[],
  roundAnalyses: [] as RoundAnalysis[],
  revolutionMesh: null as RevolutionMesh | null,
  meshCells: [] as MeshCell[],
  cylinderCells: [] as CylinderCell[],
  surfaceMetrics: null as SurfaceMetrics | null,
  surfaceClassification: 'FLAT' as SurfaceClassification,
  isLoading: false,
  error: null as string | null,
  viewMode: 'REVOLUTION_3D' as ViewMode,
  activeTab: 'CATALOG' as InfoTab,
  colorMode: 'GAUSS_HEATMAP' as ColorMode,
  wireframeEnabled: false,
  scale: 1,
  offsetX: 0,
  offsetY: 0,
  totalStitches: 0,
  totalIncreases: 0,
  totalDecreases: 0,
  logMessage: 'Listo para procesar patrón.',
};

export const useAmigurumiStore = create<AmigurumiState>((set, get) => ({
  ...INITIAL_STATE,

  parseSyntax: (text: string) => {
    set({ isLoading: true, error: null });

    const parsedRows = parse(text);
    if (parsedRows.length === 0) {
      set({
        isLoading: false,
        error: 'No se encontraron filas válidas.',
        logMessage: 'Error: Sintaxis no válida.',
      });
      return;
    }

    const meshCells = compute2DCells(parsedRows);
    const cylinderCells = compute3DProjection(parsedRows);
    const totalStitches = parsedRows.reduce((s, r) => s + r.totalCalculated, 0);
    const totalIncreases = parsedRows.reduce((s, r) => s + r.increaseCount, 0);
    const totalDecreases = parsedRows.reduce((s, r) => s + r.decreaseCount, 0);

    const roundAnalyses = computeRoundAnalyses(parsedRows);
    const surfaceMetrics = computeSurfaceMetrics(roundAnalyses);
    const surfaceClassification = classifySurface(roundAnalyses);
    const revolutionMesh = computeMesh(roundAnalyses, get().colorMode);

    set({
      syntaxText: text,
      parsedRows,
      meshCells,
      cylinderCells,
      isLoading: false,
      error: null,
      totalStitches,
      totalIncreases,
      totalDecreases,
      roundAnalyses,
      surfaceMetrics,
      surfaceClassification,
      revolutionMesh,
      logMessage: `Procesadas exitosamente ${parsedRows.length} filas. Malla 3D generada.`,
    });
  },

  reset: () => set(INITIAL_STATE),

  zoomBy: (factor: number) => {
    set((s) => ({
      scale: Math.min(MAX_SCALE, Math.max(MIN_SCALE, s.scale * factor)),
    }));
  },

  resetView: () => set({ scale: 1, offsetX: 0, offsetY: 0 }),

  setViewMode: (mode) => set({ viewMode: mode }),

  setActiveTab: (tab) => set({ activeTab: tab }),

  dragBy: (dx, dy) => {
    set((s) => ({ offsetX: s.offsetX + dx, offsetY: s.offsetY + dy }));
  },

  setColorMode: (mode) => {
    const { roundAnalyses } = get();
    const revolutionMesh = computeMesh(roundAnalyses, mode);
    set({ colorMode: mode, revolutionMesh });
  },

  setWireframe: (enabled) => set({ wireframeEnabled: enabled }),

  loadPreset: (index: number) => {
    if (index >= 0 && index < PRESETS.length) {
      get().parseSyntax(PRESETS[index].pattern);
    }
  },

  loadExample: (index: number) => {
    const examples: Record<number, string> = {
      1: '1) 8c (7p)\n2) 3p 1a 3p 1c (8p)\n3) 8p 1c (8p)\n4) 8p 1c (8p)',
      2: '1) [1a] 6v (12p)\n2) [1p 1a] 6v (18p)\n3) [2p 1a] 6v (24p)\n4) [2p 1d] 6v (18p)\n5) [1p 1d] 6v (12p)',
    };
    const text = examples[index];
    if (text) get().parseSyntax(text);
  },
}));
