import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common'; // Needed for ngClass, ngIf
import { RouterModule } from '@angular/router'; // Needed for routerLink

// Material Imports
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';

import { NavbarComponent } from './components/navbar/navbar.component';
import { MatDividerModule } from '@angular/material/divider';

@NgModule({
  declarations: [
    NavbarComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule
  ],
  exports: [
    NavbarComponent, // So App Component can use <app-navbar>
    MatToolbarModule, // Optional: export if other modules need them
    MatButtonModule,
    MatIconModule,
    MatMenuModule
  ]
})
export class SharedModule { }