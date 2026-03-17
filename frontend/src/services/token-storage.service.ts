import { Injectable } from '@angular/core';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private readonly TOKEN_KEY = 'auth-token';

  signOut(): void {
    window.localStorage.clear();
  }

  public saveToken(token: string): void {
    window.localStorage.removeItem(this.TOKEN_KEY);
    window.localStorage.setItem(this.TOKEN_KEY, token);
  }

  public getToken(): string | null {
    return window.localStorage.getItem(this.TOKEN_KEY);
  }

  // 🔥 NEW: Decode the JWT to get the user data safely
public getUser(): User | null {
  const token = this.getToken();
  // Quick length/existence check
  if (!token || token.split('.').length !== 3) return null;

  try {
    const payload = token.split('.')[1];
    
    // 1. Fix Base64 padding if necessary and decode to Percent-encoded string
    // 2. Use decodeURIComponent to handle UTF-8 characters safely
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );

    const decodedData = JSON.parse(jsonPayload);

    return {
      username: decodedData.sub || decodedData.username,
      role: decodedData.role || decodedData.roles || 'CLIENT',
      email: decodedData.email || '',
    } as User;

  } catch (e) {
    console.error("Token decoding failed", e);
    return null; 
  }
}
}