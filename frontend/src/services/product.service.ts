import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { environment } from '../environments/environment';
import { User } from '../models/user.model';

// Assuming your Gateway routes /products to the Product Service
const PRODUCT_API = `${environment.gatewayUrl}/products`;
const CATEGORY_API = `${environment.gatewayUrl}/categories`;

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor(private http: HttpClient) {}
  // product.service.ts

  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(CATEGORY_API);
  }

  // Public endpoint to get all products
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(PRODUCT_API);
  }

  createProduct(product: any): Observable<Product> {
    return this.http.post<Product>(`${PRODUCT_API}`, product);
  }
  getProductById(id: string): Observable<Product> {
    return this.http.get<Product>(`${PRODUCT_API}/${id}`);
  }
  // product.service.ts
  getUserById(userId: string): Observable<User> {
    // Matches your Gateway route for User Service
    return this.http.get<User>(`${environment.gatewayUrl}/users/${userId}`);
  }
  // product.service.ts
  getMyProducts(): Observable<Product[]> {
    // Matches your Backend: @GetMapping("/me") in ProductController
    return this.http.get<Product[]>(`${PRODUCT_API}/me`);
  }

  deleteProduct(id: string): Observable<void> {
    return this.http.delete<void>(`${PRODUCT_API}/${id}`);
  } 
 updateProduct(id: string, product: Product): Observable<any> {
  return this.http.put(`${PRODUCT_API}/${id}`, product);
} 
}
