import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule } from "@angular/forms"; 
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field"; 
import { MatInputModule } from "@angular/material/input";
import { ProductListComponent } from "./product-list/product-list.component";
import { AddProductComponent } from "./product-create/crud-product.component";
import { ProductDetailComponent } from "./product-detail/product-detail.component";
import { RouterModule } from "@angular/router";
import { SellerDashboardComponent } from "./seller-dashboard/seller-dashboard.component";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatSelectModule } from "@angular/material/select";
import { FormsModule } from '@angular/forms';  // For ngModel
import { MatTableModule } from '@angular/material/table';  // For mat-table, 
@NgModule({
  declarations: [
    ProductListComponent,
    AddProductComponent ,
    ProductDetailComponent,
    SellerDashboardComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule, 
    MatCardModule,
    FormsModule,
    MatTableModule,
    RouterModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
     MatSelectModule,
    MatProgressBarModule
  ],
  exports: [
    ProductListComponent,
    AddProductComponent,
    ProductDetailComponent,
    SellerDashboardComponent 
  ]
})
export class ProductsModule { }