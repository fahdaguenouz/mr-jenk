import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms'; // Fixes [formGroup] error

// Material Imports
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider'; // Fixes mat-divider error
import { MatFormFieldModule } from '@angular/material/form-field'; // Fixes mat-form-field error

import { ProfileComponent } from './profile.component';
import { ProfileUpdateDialogComponent } from './update/update.profile.component';
import { RouterLink, RouterModule } from '@angular/router';

@NgModule({
  declarations: [
    ProfileComponent, 
    ProfileUpdateDialogComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatDividerModule,
    MatFormFieldModule,
    RouterModule,
  ],
  exports: [
    ProfileComponent
  ]
})
export class ProfileModule { }