import {productsApi} from '../productsApi';
import {httpClient} from '../httpClient';

jest.mock('../httpClient', () => ({
  httpClient: {
    get: jest.fn(),
  },
}));

const mockGet = httpClient.get as jest.MockedFunction<typeof httpClient.get>;

const validProduct = {
  id: 1,
  title: 'Laptop',
  description: 'Developer laptop',
  price: 1299.99,
  rating: 4.8,
  thumbnail: 'https://example.com/thumb.jpg',
  images: ['https://example.com/1.jpg', 42, 'https://example.com/2.jpg'],
  category: 'technology',
  brand: 'Example',
};

describe('productsApi', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getProducts', () => {
    it('requests and normalizes the products list', async () => {
      mockGet.mockResolvedValue({products: [validProduct], total: 1});
      const signal = new AbortController().signal;

      const result = await productsApi.getProducts(signal);

      expect(mockGet).toHaveBeenCalledWith('/api/products', signal);
      expect(result).toEqual({
        products: [
          {
            ...validProduct,
            images: [
              'https://example.com/1.jpg',
              'https://example.com/2.jpg',
            ],
          },
        ],
        total: 1,
      });
    });

    it.each([null, undefined, 'invalid'])(
      'rejects an invalid response: %p',
      async response => {
        mockGet.mockResolvedValue(response);

        await expect(productsApi.getProducts()).rejects.toThrow(
          'Invalid response format',
        );
      },
    );

    it('rejects a response without a products array', async () => {
      mockGet.mockResolvedValue({products: {}, total: 1});

      await expect(productsApi.getProducts()).rejects.toThrow(
        'Invalid products data',
      );
    });

    it('rejects a response without a numeric total', async () => {
      mockGet.mockResolvedValue({products: [], total: '1'});

      await expect(productsApi.getProducts()).rejects.toThrow(
        'Invalid total count',
      );
    });

    it.each([
      ['id', 0, 'Invalid product ID'],
      ['title', 10, 'Invalid product title'],
      ['description', null, 'Invalid product description'],
      ['price', '10', 'Invalid product price'],
      ['rating', undefined, 'Invalid product rating'],
      ['thumbnail', false, 'Invalid product thumbnail'],
    ])('rejects an invalid %s', async (field, value, message) => {
      mockGet.mockResolvedValue({
        products: [{...validProduct, [field]: value}],
        total: 1,
      });

      await expect(productsApi.getProducts()).rejects.toThrow(message);
    });
  });

  describe('getProductById', () => {
    it('requests a product by ID and applies optional defaults', async () => {
      mockGet.mockResolvedValue({
        ...validProduct,
        images: null,
        category: 123,
        brand: undefined,
      });
      const signal = new AbortController().signal;

      const result = await productsApi.getProductById(1, signal);

      expect(mockGet).toHaveBeenCalledWith('/api/products/1', signal);
      expect(result.images).toEqual([]);
      expect(result.category).toBeUndefined();
      expect(result.brand).toBeNull();
    });

    it.each([0, -1, 1.5, Number.NaN])(
      'rejects invalid product ID %p without making a request',
      async productId => {
        await expect(productsApi.getProductById(productId)).rejects.toThrow(
          'Invalid product ID',
        );
        expect(mockGet).not.toHaveBeenCalled();
      },
    );

    it('rejects invalid product data', async () => {
      mockGet.mockResolvedValue(null);

      await expect(productsApi.getProductById(1)).rejects.toThrow(
        'Invalid product data',
      );
    });
  });
});
