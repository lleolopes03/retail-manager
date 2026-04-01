import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class EmpresaService {
  private apiUrl = 'http://localhost:8080/api/v1/empresa';
  private cepUrl = 'http://localhost:8080/cep';

  constructor(private http: HttpClient) {}

  cadastrarEmpresa(empresa: any): Observable<any> {
    return this.http.post(this.apiUrl, empresa);
  }

  buscarEmpresa(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  atualizarEmpresa(id: number, empresa: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, empresa);
  }

  buscarCep(cep: string): Observable<any> {
    return this.http.get(`${this.cepUrl}?cep=${cep}`);
  }
}
