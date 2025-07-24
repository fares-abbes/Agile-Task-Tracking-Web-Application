import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LandingPageComponent} from "./landing-page/landing-page.component";
import {AuthentificationComponent} from "./authentification/authentification.component";
import { ListUsersComponent } from './list-users/list-users.component';
import { NavbarComponent } from './navbar/navbar.component';

const routes: Routes = [
  { path: 'auth', component: AuthentificationComponent },
  { path: 'landing', component: LandingPageComponent },
    { path: 'listusers', component: ListUsersComponent },
        { path: 'navbar', component: NavbarComponent },


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
