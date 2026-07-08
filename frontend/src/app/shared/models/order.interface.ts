import { UserResponse } from './auth.interface';

export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface OrderRequest {
  items: OrderItemRequest[];
}

export interface OrderResponse {
  id: number;
  orderDate: string;
  status: string;
  totalAmount: number;
  user: UserResponse; // Ahora está definido
  items: OrderItemResponse[];
}

export interface OrderItemResponse {
  productId: number;
  productName: string;
  quantity: number;
  priceAtOrder: number;
}
