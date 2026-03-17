import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select'; // Added for Registration Role Selection

import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { RouterModule } from '@angular/router';


@NgModule({
  declarations: [
    LoginComponent,
    RegisterComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    RouterModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatSelectModule
  ],
  exports: [
    LoginComponent,
    RegisterComponent
  ]
})
export class AuthModule { }