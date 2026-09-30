import {formatCurrency} from '../currency';

describe('formatCurrency', () => {
  it('formats price correctly', () => {
    expect(formatCurrency(29.99)).toBe('$29.99');
  });

  it('formats whole numbers', () => {
    expect(formatCurrency(100)).toBe('$100.00');
  });

  it('formats zero', () => {
    expect(formatCurrency(0)).toBe('$0.00');
  });

  it('formats large numbers', () => {
    expect(formatCurrency(1234.56)).toBe('$1,234.56');
  });
});
