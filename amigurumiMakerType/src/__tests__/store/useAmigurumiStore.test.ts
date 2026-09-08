// src/__tests__/store/useAmigurumiStore.test.ts

import { useAmigurumiStore } from '../../store/useAmigurumiStore';

// Reset store before each test
beforeEach(() => {
  useAmigurumiStore.getState().reset();
});

describe('useAmigurumiStore', () => {
  describe('initial state', () => {
    it('has correct defaults', () => {
      const state = useAmigurumiStore.getState();
      expect(state.syntaxText).toBe('');
      expect(state.parsedRows).toEqual([]);
      expect(state.isLoading).toBe(false);
      expect(state.error).toBeNull();
      expect(state.colorMode).toBe('GAUSS_HEATMAP');
      expect(state.wireframeEnabled).toBe(false);
      expect(state.scale).toBe(1);
    });
  });

  describe('parseSyntax', () => {
    it('parses valid pattern and updates all fields', () => {
      const { parseSyntax } = useAmigurumiStore.getState();
      parseSyntax('1) 6c (6p)\n2) [1a] 6v (12p)');

      const state = useAmigurumiStore.getState();
      expect(state.parsedRows).toHaveLength(2);
      expect(state.roundAnalyses.length).toBeGreaterThan(0);
      expect(state.revolutionMesh).not.toBeNull();
      expect(state.isLoading).toBe(false);
      expect(state.error).toBeNull();
    });

    it('sets error for invalid input', () => {
      const { parseSyntax } = useAmigurumiStore.getState();
      parseSyntax('');

      const state = useAmigurumiStore.getState();
      expect(state.error).toContain('No se encontraron filas');
    });

    it('computes totalStitches', () => {
      const { parseSyntax } = useAmigurumiStore.getState();
      parseSyntax('2) [1a] 6v (12p)');

      const state = useAmigurumiStore.getState();
      expect(state.totalStitches).toBe(12);
    });
  });

  describe('zoomBy', () => {
    it('zooms in', () => {
      useAmigurumiStore.getState().zoomBy(1.5);
      expect(useAmigurumiStore.getState().scale).toBeCloseTo(1.5, 2);
    });

    it('clamps to MAX_SCALE', () => {
      useAmigurumiStore.getState().zoomBy(100);
      expect(useAmigurumiStore.getState().scale).toBe(4);
    });

    it('clamps to MIN_SCALE', () => {
      useAmigurumiStore.getState().zoomBy(0.01);
      expect(useAmigurumiStore.getState().scale).toBe(0.3);
    });
  });

  describe('resetView', () => {
    it('resets scale and offset', () => {
      useAmigurumiStore.getState().zoomBy(2);
      useAmigurumiStore.getState().dragBy(10, 20);
      useAmigurumiStore.getState().resetView();

      const state = useAmigurumiStore.getState();
      expect(state.scale).toBe(1);
      expect(state.offsetX).toBe(0);
      expect(state.offsetY).toBe(0);
    });
  });

  describe('setColorMode', () => {
    it('recomputes revolution mesh', () => {
      const { parseSyntax } = useAmigurumiStore.getState();
      parseSyntax('1) 6c (6p)\n2) [1a] 6v (12p)');

      const meshBefore = useAmigurumiStore.getState().revolutionMesh;
      useAmigurumiStore.getState().setColorMode('ROW_GRADIENT');
      const meshAfter = useAmigurumiStore.getState().revolutionMesh;

      expect(meshAfter).not.toBeNull();
      // Colors should differ between modes
      if (meshBefore && meshAfter) {
        expect(meshBefore.segments[0].color).not.toEqual(meshAfter.segments[0].color);
      }
    });
  });

  describe('loadPreset', () => {
    it('loads preset by index', () => {
      useAmigurumiStore.getState().loadPreset(0); // Esfera

      const state = useAmigurumiStore.getState();
      expect(state.syntaxText).toContain('6c');
      expect(state.parsedRows.length).toBeGreaterThan(0);
    });

    it('ignores invalid index', () => {
      useAmigurumiStore.getState().loadPreset(99);
      expect(useAmigurumiStore.getState().parsedRows).toEqual([]);
    });
  });

  describe('reset', () => {
    it('clears all state', () => {
      const { parseSyntax } = useAmigurumiStore.getState();
      parseSyntax('2) [1a] 6v (12p)');

      useAmigurumiStore.getState().reset();
      const state = useAmigurumiStore.getState();

      expect(state.parsedRows).toEqual([]);
      expect(state.syntaxText).toBe('');
      expect(state.revolutionMesh).toBeNull();
    });
  });

  describe('dragBy', () => {
    it('updates offset', () => {
      useAmigurumiStore.getState().dragBy(5, 10);
      const state = useAmigurumiStore.getState();
      expect(state.offsetX).toBe(5);
      expect(state.offsetY).toBe(10);
    });
  });
});
