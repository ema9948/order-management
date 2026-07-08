export interface ProductRequest {
  name: string;
  description?: string;
  price: number;
  stock: number;
}

export interface ProductResponse extends ProductRequest {
  id: number;
}
