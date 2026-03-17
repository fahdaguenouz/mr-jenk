import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { ProductService } from "../../../services/product.service";
import { ToasterService } from "../../../shared/components/Toaster/toast";
import { Product } from "../../../models/product.model";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";

@Component({
  selector: 'app-seller-dashboard',
  templateUrl: './seller-dashboard.component.html',
  styleUrls: ['./seller-dashboard.component.scss'],
  standalone: false,
})
export class SellerDashboardComponent implements OnInit {
  @ViewChild('deleteDialog') deleteDialog!: TemplateRef<any>;

  myProducts: Product[] = [];
  filteredProducts: Product[] = [];
  searchTerm: string = '';
  displayedColumns: string[] = ['image', 'price', 'stock', 'status', 'actions'];
  
  // Stats
  totalStock: number = 0;
  totalValue: number = 0;
  lowStockCount: number = 0;

  private deleteDialogRef: MatDialogRef<any> | null = null;
  private productToDelete: Product | null = null;

  constructor(
    private productService: ProductService, 
    private toast: ToasterService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getMyProducts().subscribe({
      next: (data) => {
        this.myProducts = data;
        this.filteredProducts = data;
        this.calculateStats();
      },
      error: () => this.toast.showError('Failed to load products')
    });
  }

  calculateStats() {
    this.totalStock = this.myProducts.reduce((sum, p) => sum + (p.stockQuantity || 0), 0);
    this.totalValue = this.myProducts.reduce((sum, p) => sum + ((p.price || 0) * (p.stockQuantity || 0)), 0);
    this.lowStockCount = this.myProducts.filter(p => p.stockQuantity < 5 && p.stockQuantity > 0).length;
  }

  filterProducts() {
    if (!this.searchTerm) {
      this.filteredProducts = this.myProducts;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredProducts = this.myProducts.filter(p => 
        p.name.toLowerCase().includes(term) || 
        p.category?.toLowerCase().includes(term)
      );
    }
  }

  openDeleteDialog(product: Product) {
    this.productToDelete = product;
    this.deleteDialogRef = this.dialog.open(this.deleteDialog, {
      data: { product },
      panelClass: 'delete-dialog-panel',
      autoFocus: false
    });
  }

  closeDialog() {
    if (this.deleteDialogRef) {
      this.deleteDialogRef.close();
      this.deleteDialogRef = null;
      this.productToDelete = null;
    }
  }

  confirmDelete(productId: string) {
    this.productService.deleteProduct(productId).subscribe({
      next: () => {
        this.toast.showSuccess('Product deleted successfully');
        this.myProducts = this.myProducts.filter(p => p.id !== productId);
        this.filteredProducts = this.filteredProducts.filter(p => p.id !== productId);
        this.calculateStats();
        this.closeDialog();
      },
      error: () => {
        this.toast.showError('Failed to delete product');
        this.closeDialog();
      }
    });
  }
}