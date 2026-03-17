import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { TokenStorageService } from '../../services/token-storage.service';
import { ToasterService } from '../../shared/components/Toaster/toast';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private tokenStorage: TokenStorageService,
    private router: Router,
    private toast: ToasterService
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  return next.handle(request).pipe(
    catchError((err: HttpErrorResponse) => {
      const isLoginRequest = request.url.includes('/auth/login');

      if ([401, 403].includes(err.status) && !isLoginRequest) {
        // Only force logout/redirect if it's NOT the login page
        this.tokenStorage.signOut();
        this.toast.showError('Session expired. Please log in again.');
        window.location.href = '/login'; 
      }

      // Pass the error back so the LoginComponent can catch it in its own .subscribe(error)
      return throwError(() => err);
    })
  );
}
}