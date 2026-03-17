export interface Product {
  id?: string;
  name: string;
  description: string;
  price: number;
  sellerId: string;
  stockQuantity: number;
  category: string;
  mediaIds: string[]; 
  createdAt?: Date;
}