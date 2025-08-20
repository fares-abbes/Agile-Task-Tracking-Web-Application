import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';

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
import { ViewTasksComponent } from './view-tasks/view-tasks.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { TestReportComponent } from './test-report/test-report.component';
import { ViewTestReportsComponent } from './view-test-reports/view-test-reports.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { StatsComponent } from './stats/stats.component';
import { GenerateTasksComponent } from './generate-tasks/generate-tasks.component';
import { ScrollAreaComponent } from './scroll-area/scroll-area.component';


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
    ClientCrudComponent,
    ViewTasksComponent,
    SidebarComponent,
    TestReportComponent,
    ViewTestReportsComponent,
    DashboardComponent,
    StatsComponent,
    GenerateTasksComponent,
    ScrollAreaComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgChartsModule,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
