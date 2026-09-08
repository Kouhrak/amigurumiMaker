import { STITCH_WIDTH, STITCH_HEIGHT, GAUSS_POSITIVE_COLOR, GAUSS_NEGATIVE_COLOR, GAUSS_NEUTRAL_COLOR, MIN_SEGMENTS } from '../../domain/constants';

describe('Constants', () => {
  it('STITCH_WIDTH matches Kotlin (0.5)', () => {
    expect(STITCH_WIDTH).toBe(0.5);
  });

  it('STITCH_HEIGHT matches Kotlin (0.5)', () => {
    expect(STITCH_HEIGHT).toBe(0.5);
  });

  it('GAUSS_POSITIVE_COLOR is green', () => {
    expect(GAUSS_POSITIVE_COLOR).toEqual({ x: 0.06, y: 0.72, z: 0.50 });
  });

  it('GAUSS_NEGATIVE_COLOR is pink', () => {
    expect(GAUSS_NEGATIVE_COLOR).toEqual({ x: 0.95, y: 0.25, z: 0.37 });
  });

  it('GAUSS_NEUTRAL_COLOR is cyan', () => {
    expect(GAUSS_NEUTRAL_COLOR).toEqual({ x: 0.02, y: 0.71, z: 0.83 });
  });

  it('MIN_SEGMENTS is 4', () => {
    expect(MIN_SEGMENTS).toBe(4);
  });
});
