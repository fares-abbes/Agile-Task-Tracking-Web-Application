import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-client-crud',
  templateUrl: './client-crud.component.html'
})
export class ClientCrudComponent implements OnInit {
  clients: any[] = [];
  loading = true;
  error = false;
  showAddForm = false;
  addClientForm = new FormGroup({
    clientName: new FormControl('', Validators.required)
  });
  editClientId: number | null = null;
  editClientForm = new FormGroup({
    clientName: new FormControl('', Validators.required)
  });

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchClients();
  }

  fetchClients() {
    this.loading = true;
    this.http.get<any[]>('http://localhost:8090/api/clients/GetAllClients').subscribe({
      next: data => {
        this.clients = data;
        this.loading = false;
      },
      error: () => {
        this.error = true;
        this.loading = false;
      }
    });
  }

  addClient() {
    if (this.addClientForm.invalid) return;
    this.http.post('http://localhost:8090/api/clients/add', this.addClientForm.value).subscribe({
      next: () => {
        this.addClientForm.reset();
        this.fetchClients();
      }
    });
  }

  startEdit(client: any) {
    this.editClientId = client.clientId;
    this.editClientForm.setValue({ clientName: client.clientName });
  }

  updateClient() {
    if (this.editClientForm.invalid || this.editClientId === null) return;
    this.http.put(`http://localhost:8090/api/clients/Update/${this.editClientId}`, this.editClientForm.value).subscribe({
      next: () => {
        this.editClientId = null;
        this.fetchClients();
      }
    });
  }

  deleteClient(clientId: number) {
    this.http.delete(`http://localhost:8090/api/clients/Delete/${clientId}`).subscribe({
      next: () => this.fetchClients()
    });
  }

  cancelEdit() {
    this.editClientId = null;
  }
}
