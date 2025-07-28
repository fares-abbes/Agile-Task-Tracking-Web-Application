import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html'
})
export class CreateUserComponent {
  createUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
    role: new FormControl('DEVELOPPER', Validators.required)
  });

  errorMessage = '';
  loading = false;

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    if (this.createUserForm.invalid) return;
    this.loading = true;
    this.errorMessage = '';
    this.http.post('http://localhost:8090/api/users', this.createUserForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/listusers']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to create user.';
      }
    });
  }
}
