import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  isMobileMenuOpen = false;
  currentUser: any = null;

  constructor(private router: Router) {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      this.currentUser = JSON.parse(userStr);
    }
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  getUserInitials(): string {
    const currentUserStr = localStorage.getItem('currentUser');
    if (!currentUserStr) return '?';

    try {
      const currentUser = JSON.parse(currentUserStr);
      const username = currentUser.username || '';

      if (!username) return '?';
      return username.charAt(0).toUpperCase();
    } catch (error) {
      console.error('Error parsing user data:', error);
      return '?';
    }
  }

  isDeveloper(): boolean {
    const currentUserStr = localStorage.getItem('currentUser');
    if (!currentUserStr) return false;

    try {
      const currentUser = JSON.parse(currentUserStr);
      // Check if role is DEVELOPER (adjust the exact role name as needed)
      return currentUser.role === 'DEVELOPPER';
    } catch (error) {
      console.error('Error parsing user data:', error);
      return false;
    }
  }

  logout() {
    // Clear local storage
    localStorage.removeItem('currentUser');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    // Navigate to login page
    this.router.navigate(['/auth']);
  }

  goToProjects() {
    const currentUserStr = localStorage.getItem('currentUser');
    if (!currentUserStr) {
      this.router.navigate(['/auth']);
      return;
    }
    const currentUser = JSON.parse(currentUserStr);
    if (currentUser.role === 'ADMIN') {
      this.router.navigate(['/projects'], { queryParams: { type: 'all' } });
    } else if (currentUser.role === 'TEAMLEAD') {
      this.router.navigate(['/projects'], { queryParams: { teamLeadId: currentUser.id } });
    } else {
      this.router.navigate(['/projects']);
    }
  }

  goToReports() {
    if (!this.currentUser) return;
    if (this.currentUser.role === 'TEAMLEAD') {
      // Route to the reports page and let the component load team lead reports
      this.router.navigate(['/my-task-reports'], { queryParams: { type: 'teamlead' } });
    } else if (this.currentUser.role === 'DEVELOPER') {
      // Route to the reports page and let the component load developer reports
      this.router.navigate(['/my-task-reports'], { queryParams: { type: 'developer' } });
    }
    // Add more roles if needed
  }
}
