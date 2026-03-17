import { Routes } from '@angular/router';
import { LoginComponent } from '../features/auth/login/login.component';
import { NoAuthGuard } from '../core/guards/no-auth.guard';
import { RegisterComponent } from '../features/auth/register/register.component';
import { AuthGuard } from '../core/guards/auth.guard';
import { ProductListComponent } from '../features/products/product-list/product-list.component';
import { ProfileComponent } from '../features/profile/profile.component';
import { AddProductComponent } from '../features/products/product-create/crud-product.component';
import { ProductDetailComponent } from '../features/products/product-detail/product-detail.component';
import { SellerDashboardComponent } from '../features/products/seller-dashboard/seller-dashboard.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [NoAuthGuard], // If logged in, go to home
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [NoAuthGuard],
  },

  // Example of a protected route we will build soon
  {
    path: 'products',
    component: ProductListComponent, // Placeholder for now
    canActivate: [AuthGuard],
  },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  {
    path: 'add-product',
    component: AddProductComponent,
    canActivate: [AuthGuard],
    data: { expectedRole: 'SELLER' },
  },

  {
    path: 'add-product/:id',
    component: AddProductComponent,
    canActivate: [AuthGuard],
    data: { expectedRole: 'SELLER' },
  },
  {
    path: 'seller-dashboard',
    component: SellerDashboardComponent,
    canActivate: [AuthGuard],
    data: { expectedRole: 'SELLER' },
  },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' },
];
