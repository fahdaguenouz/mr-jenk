import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class MediaService {
  constructor(private http: HttpClient) {}

  uploadImage(file: File): Observable<any> {
    const formData = new FormData();
    // The name 'file' matches your @RequestParam("file") perfectly
    formData.append('file', file); 

    // 🔥 FIX: Changed from /images to /upload
    return this.http.post(`${environment.gatewayUrl}/api/media/upload`, formData);
  }
}