import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-authentification',
  templateUrl: './authentification.component.html',
  styleUrls: ['./authentification.component.css']
})
export class AuthentificationComponent {
  username: string = '';
  password: string = '';
  loading: boolean = false;
  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  onLogin() {
    this.loading = true;
    this.errorMessage = '';
    this.http.post<any>('http://localhost:8090/auth/login', {
      username: this.username,
      password: this.password
    }).subscribe({
      next: (res) => {
        // Store tokens
        localStorage.setItem('accessToken', res.accessToken);

        // Store user information
        localStorage.setItem('currentUser', JSON.stringify({
          id: res.id,
          username: res.username,
          email: res.email,
          role: res.role,
          // Add any other user properties you need
        }));

        this.loading = false;
        this.router.navigate(['projects']);
      },
      error: (err) => {
        console.error('Login error:', err);
        this.loading = false;
        this.errorMessage = 'Invalid username or password.';
      }
    });
  }
}
