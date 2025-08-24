import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserTaskRankDTO {
  userId: number;
  username: string;
  teamId: number;
  tasksDone: number;
  teamLeadId: number;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private baseUrl = 'http://localhost:8090/api/tasks/'; // adjust to your backend

  constructor(private http: HttpClient) {}

  getTeamRank(teamId: number): Observable<UserTaskRankDTO[]> {
    return this.http.get<UserTaskRankDTO[]>(`${this.baseUrl}/team/${teamId}/rank-done`);
  }
}
