import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { TokenStorageService } from '../../../services/token-storage.service';
import { ToasterService } from '../../../shared/components/Toaster/toast';

@Component({
  standalone: false,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  errorMessage = '';
  hidePassword = true;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router,
    private toast: ToasterService
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

// Inside your onSubmit() method:
onSubmit(): void {
  this.isLoading = true;
  if (this.loginForm.valid) {
    this.authService.login(this.loginForm.value).subscribe({
      next: (data) => {
        // 1. Just save the token!
        this.tokenStorage.saveToken(data.token);
        
        // 2. Retrieve the decoded user from the token we just saved
        const decodedUser = this.tokenStorage.getUser();
        
        if (decodedUser) {
          this.authService.setLoggedInUser(decodedUser);
          this.toast.showSuccess('Login successful!');
          this.router.navigate(['/products']);
        }
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Invalid username or password.';
        this.errorMessage = errorMsg;
        this.toast.showError(errorMsg);
      },
    });
  }
}
}
