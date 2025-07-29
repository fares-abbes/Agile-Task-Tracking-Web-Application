import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LandingPageComponent} from "./landing-page/landing-page.component";
import {AuthentificationComponent} from "./authentification/authentification.component";
import { ListUsersComponent } from './list-users/list-users.component';
import { NavbarComponent } from './navbar/navbar.component';
import { ProjectListComponent } from './project-list/project-list.component';
import { ProjectTasksComponent } from './project-tasks/project-tasks.component';
import { DeveloperTasksComponent } from './developer-tasks/developer-tasks.component';
import { CreateUserComponent } from './create-user/create-user.component';
import { CreateProjectComponent } from './create-project/create-project.component';
import { CreateTaskComponent } from './create-task/create-task.component';

const routes: Routes = [
  { path: 'auth', component: AuthentificationComponent },
  { path: 'landing', component: LandingPageComponent },
    { path: 'listusers', component: ListUsersComponent },
        { path: 'navbar', component: NavbarComponent },
  { path: 'projects', component: ProjectListComponent },
                  { path: 'projects/:projectId/tasks', component: ProjectTasksComponent },
  { path: '', component: ListUsersComponent },
      { path: 'my-tasks', component: DeveloperTasksComponent },
            { path: 'create-user', component: CreateUserComponent },
            { path: 'create-project', component: CreateProjectComponent },
            { path: 'create-task', component: CreateTaskComponent },







];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
