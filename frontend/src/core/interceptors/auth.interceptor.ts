import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from '../../services/token-storage.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private tokenStorage: TokenStorageService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenStorage.getToken();
    
    // Clone the request and add the Authorization header
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request);
  }
}