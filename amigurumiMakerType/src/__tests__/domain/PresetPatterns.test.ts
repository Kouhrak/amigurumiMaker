// src/__tests__/domain/PresetPatterns.test.ts

import { PRESETS } from '../../domain/PresetPatterns';

describe('PresetPatterns', () => {
  it('has 5 presets', () => {
    expect(PRESETS).toHaveLength(5);
  });

  it('each preset has name, description, and pattern', () => {
    for (const preset of PRESETS) {
      expect(typeof preset.name).toBe('string');
      expect(typeof preset.description).toBe('string');
      expect(typeof preset.pattern).toBe('string');
      expect(preset.name.length).toBeGreaterThan(0);
      expect(preset.pattern.length).toBeGreaterThan(0);
    }
  });

  it('first preset is Esfera', () => {
    expect(PRESETS[0].name).toBe('Esfera');
    expect(PRESETS[0].description).toContain('positiva');
  });

  it('second preset is Hiperbólica', () => {
    expect(PRESETS[1].name).toBe('Hiperbólica');
    expect(PRESETS[1].description).toContain('negativa');
  });

  it('patterns contain newlines (multi-line)', () => {
    for (const preset of PRESETS) {
      expect(preset.pattern).toContain('\n');
    }
  });

  it('patterns use valid syntax characters', () => {
    const validChars = /^[0-9pacd\[\]v\(\)\s\n]+$/;
    for (const preset of PRESETS) {
      expect(preset.pattern).toMatch(validChars);
    }
  });
});
