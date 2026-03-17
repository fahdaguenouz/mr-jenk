import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthModule } from '../features/auth/auth.module';
import { SharedModule } from '../shared/shared.module';
import { ProfileModule } from '../features/profile/profile.module';
import { ProductsModule } from '../features/products/products.module';

@Component({
  selector: 'app-root',
  standalone: true, // Ensure this is true
  imports: [RouterOutlet, AuthModule,SharedModule,ProfileModule,ProductsModule], // Add AuthModule here
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('01buy Frontend');
}