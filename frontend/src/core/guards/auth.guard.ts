// auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { TokenStorageService } from '../../services/token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private tokenStorage: TokenStorageService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const user = this.tokenStorage.getUser();

    // 1. Check if logged in
    if (!user) {
      this.router.navigate(['/login']);
      return false;
    }

    // 2. Check for required roles (passed via route data)
    const expectedRole = route.data['expectedRole'];
    
    if (expectedRole && user.role !== expectedRole) {
      this.router.navigate(['/products']); // Redirect unauthorized users
      return false;
    }

    return true;
  }
}