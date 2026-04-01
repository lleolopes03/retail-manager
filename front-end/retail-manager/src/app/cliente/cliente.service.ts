import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cliente {
  id?: number;
  nome?: string;
  dataNascimento?: string;
  cpf?: string;
  email?: string;
  telefone?: string;
  endereco?: {
    cep?: string;
    rua?: string;
    bairro?: string;
    cidade?: string;
    estado?: string;
  };
}

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private apiUrl = 'http://localhost:8080/api/v1/clientes'; // ✅ alinhado com seu back-end
  private cepUrl = 'http://localhost:8080/cep';

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }

  criar(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  atualizar(id: number, cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}`, cliente);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  buscarCep(cep: string): Observable<any> {
    return this.http.get(`${this.cepUrl}?cep=${cep}`);
  }
}
