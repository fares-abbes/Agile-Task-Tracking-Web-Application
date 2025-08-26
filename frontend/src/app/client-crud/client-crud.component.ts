import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-client-crud',
  templateUrl: './client-crud.component.html',
  styleUrls: ['./client-crud.component.css']
})
export class ClientCrudComponent implements OnInit {
  clients: any[] = [];
  loading = false;
  error = false;

  showAddForm = false;
  addClientForm = new FormGroup({
    clientName: new FormControl('', Validators.required),
    contactEmail: new FormControl(''),
    phone: new FormControl('')
  });

  editClientId: number | null = null;
  editClientForm = new FormGroup({
    clientName: new FormControl('', Validators.required),
    contactEmail: new FormControl(''),
    phone: new FormControl('')
  });

  // Keep the exact backend routes you used
  private baseGet = 'http://localhost:8090/api/clients/GetAllClients';
  private addUrl = 'http://localhost:8090/api/clients/add';
  private updateUrl = (id: number) => `http://localhost:8090/api/clients/Update/${id}`;
  private deleteUrl = (id: number) => `http://localhost:8090/api/clients/Delete/${id}`;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchClients();
  }

  fetchClients() {
    this.loading = true;
    this.error = false;
    console.log('[ClientCrud] GET', this.baseGet);
    this.http.get<any[]>(this.baseGet).subscribe({
      next: data => {
        this.clients = Array.isArray(data) ? data : [];
        this.loading = false;
      },
      error: (err) => {
        console.error('[ClientCrud] fetch failed', err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  toggleAdd() {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) this.addClientForm.reset();
  }

  addClient() {
    if (this.addClientForm.invalid) return;
    this.http.post(this.addUrl, this.addClientForm.value).subscribe({
      next: () => {
        this.addClientForm.reset();
        this.showAddForm = false;
        this.fetchClients();
      },
      error: (err) => console.error('[ClientCrud] add failed', err)
    });
  }

  startEdit(client: any) {
    this.editClientId = client.clientId;
    this.editClientForm.setValue({
      clientName: client.clientName || '',
      contactEmail: client.contactEmail || '',
      phone: client.phone || ''
    });
  }

  updateClient() {
    if (this.editClientForm.invalid || this.editClientId === null) return;
    this.http.put(this.updateUrl(this.editClientId), this.editClientForm.value).subscribe({
      next: () => {
        this.editClientId = null;
        this.fetchClients();
      },
      error: (err) => console.error('[ClientCrud] update failed', err)
    });
  }

  cancelEdit() {
    this.editClientId = null;
  }

  deleteClient(clientId: number) {
    if (!confirm('Delete this client?')) return;
    this.http.delete(this.deleteUrl(clientId)).subscribe({
      next: () => this.fetchClients(),
      error: (err) => console.error('[ClientCrud] delete failed', err)
    });
  }
}
