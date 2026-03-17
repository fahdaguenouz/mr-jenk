import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { AuthResponse, User } from '../models/user.model';
import { TokenStorageService } from './token-storage.service';

const AUTH_API = `${environment.gatewayUrl}/auth/`;

@Injectable({ providedIn: 'root' })
export class AuthService {

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$: Observable<User | null> = this.currentUserSubject.asObservable();

  constructor(private tokenStorage: TokenStorageService,private http: HttpClient) {
    // On app start, check if a user is already in storage
    const savedUser = this.tokenStorage.getUser();
    if (savedUser) {
      this.currentUserSubject.next(savedUser);
    }
  }

  // Call this in LoginComponent after successful login
  setLoggedInUser(user: User): void {
    this.currentUserSubject.next(user);
  }

  // Call this during logout
  clearUser(): void {
    this.currentUserSubject.next(null);
  }

  login(credentials: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(AUTH_API + 'login', {
      identifier: credentials.username,
      password: credentials.password,
    });
  }

  register(user: any): Observable<any> {
    return this.http.post(AUTH_API + 'register', {
      firstName: user.firstName,
      lastName: user.lastName,
      username: user.username,
      email: user.email,
      password: user.password,
      role: user.role, // CLIENT or SELLER
    });
  }
}
