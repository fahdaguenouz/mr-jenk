import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from '../../../services/token-storage.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  standalone: false,
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;
  username?: string;
  role?: string;

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    // Check initial state
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      this.username = user?.username;
      this.role = user?.role;
    });
  }

  logout(): void {
    this.tokenStorage.signOut();
    this.authService.clearUser(); 
    this.router.navigate(['/login']);
  }
}
