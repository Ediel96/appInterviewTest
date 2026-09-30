export interface Product {
  id: number;
  title: string;
  description: string;
  price: number;
  rating: number;
  thumbnail: string;
  images: string[];
  category?: string;
  brand?: string | null;
}

export interface ProductListResponse {
  products: Product[];
  total: number;
}
