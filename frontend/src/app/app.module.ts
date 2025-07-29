import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // <-- Import ReactiveFormsModule

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { AuthentificationComponent } from './authentification/authentification.component';
import { ListUsersComponent } from './list-users/list-users.component';
import { NavbarComponent } from './navbar/navbar.component';
import { ProjectListComponent } from './project-list/project-list.component';
import { ProjectTasksComponent } from './project-tasks/project-tasks.component';
import { DeveloperTasksComponent } from './developer-tasks/developer-tasks.component';
import { AuthInterceptor } from "./interceptors/auth.interceptor";
import { CreateUserComponent } from './create-user/create-user.component';
import { CreateProjectComponent } from './create-project/create-project.component';
import { CreateTaskComponent } from './create-task/create-task.component';
import { ClientCrudComponent } from './client-crud/client-crud.component';

@NgModule({
  declarations: [
    AppComponent,
    LandingPageComponent,
    AuthentificationComponent,
    ListUsersComponent,
    NavbarComponent,
    ProjectListComponent,
    ProjectTasksComponent,
    DeveloperTasksComponent,
    CreateUserComponent,
    CreateProjectComponent,
    CreateTaskComponent,
    ClientCrudComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
