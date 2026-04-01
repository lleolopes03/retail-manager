import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WhatsappService {
  private apiUrl = '/api/v1/whatsapp';

  constructor(private http: HttpClient) {}

  enviarRecibo(clienteId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/send/${clienteId}`, {});
  }

  enviarMensagemPersonalizada(clienteId: string, texto: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/send/${clienteId}`, { texto });
  }
}
