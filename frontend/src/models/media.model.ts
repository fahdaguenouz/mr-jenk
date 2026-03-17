export interface MediaMetadata {
  id: string;
  name: string;
  type: string; // e.g., 'image/png'
  size: number; // Bytes
  url: string;
}

export interface UploadResponse {
  mediaId: string;
  url: string;
  message: string;
}