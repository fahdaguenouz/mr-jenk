import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { TokenStorageService } from '../../services/token-storage.service';

@Injectable({ providedIn: 'root' })
export class NoAuthGuard implements CanActivate {
  constructor(private tokenStorage: TokenStorageService, private router: Router) {}

  canActivate(): boolean {
    if (this.tokenStorage.getToken()) {
      this.router.navigate(['/products']); // Send them away from login if they have a token
      return false;
    }
    return true;
  }
}