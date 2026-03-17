import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { User } from '../../../models/user.model';
import { ToasterService } from '../../../shared/components/Toaster/toast';

@Component({
  selector: 'app-profile-update-dialog',
  templateUrl: './profile-update.component.html',
  styleUrls: ['./profile-update.component.scss'],
  standalone: false
})
export class ProfileUpdateDialogComponent implements OnInit {
  updateForm: FormGroup;
  avatarPreview: string | ArrayBuffer | null = null;
  selectedFile: File | null = null;
  isUploading = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<ProfileUpdateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: User,
    private toast: ToasterService
  ) {
    this.updateForm = this.fb.group({
      firstName: [data.firstName || '', [Validators.maxLength(50)]],
      lastName: [data.lastName || '', [Validators.maxLength(50)]]
    });
    this.avatarPreview = data.avatarMediaId || 'assets/images/default-avatar.svg';
  }

  ngOnInit(): void {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.match(/image\/(jpeg|jpg|png)/)) {
      this.toast.showError('Only JPG and PNG images are allowed');
      return;
    }

    // Validate file size (2MB)
    if (file.size > 2 * 1024 * 1024) {
      this.toast.showError('Image must be less than 2MB');
      return;
    }

    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => this.avatarPreview = reader.result;
    reader.readAsDataURL(file);
  }

  onSave() {
    if (this.updateForm.invalid) return;
    
    this.isUploading = true;
    this.dialogRef.close({
      ...this.updateForm.value,
      file: this.selectedFile
    });
  }
}