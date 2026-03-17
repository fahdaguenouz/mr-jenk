import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class MediaService {
  constructor(private http: HttpClient) {}

  uploadImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file); 
    return this.http.post(`${environment.gatewayUrl}/api/media/upload`, formData);
  }

  // 🔥 NEW BATCH UPLOAD METHOD
  uploadMultipleImages(files: File[]): Observable<any> {
    const formData = new FormData();
    // Append every file to the exact same 'file' key, creating a list!
    files.forEach(file => formData.append('file', file)); 
    
    return this.http.post(`${environment.gatewayUrl}/api/media/upload`, formData);
  }
}