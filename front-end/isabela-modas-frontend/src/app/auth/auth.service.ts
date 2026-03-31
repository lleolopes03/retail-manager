import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, BehaviorSubject, map } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

// Interface para resposta do login
export interface LoginResponse {
  token: string;
  refreshToken: string;
}


// Interface para credenciais de login
export interface LoginCredentials {
  login: string;
  senha: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  private roles: string[] = [];
  private userName: string = '';

  // 🔹 BehaviorSubject para token reativo
  private tokenSubject = new BehaviorSubject<string | null>(this.getToken());

  constructor(private http: HttpClient) {}

  login(credentials: LoginCredentials): Observable<LoginResponse> {
  return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials)
    .pipe(
      tap((response: LoginResponse) => {
        this.saveToken(response.token);
        localStorage.setItem('refreshToken', response.refreshToken); // 🔹 salva refresh
      })
    );
}


  private saveToken(token: string) {
    localStorage.setItem('token', token);
    this.decodeToken(token);
    this.tokenSubject.next(token); // 🔹 notifica que o token mudou
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // 🔹 Observable para componentes se inscreverem
  getTokenObservable(): Observable<string | null> {
    return this.tokenSubject.asObservable();
  }

  logout() {
    localStorage.removeItem('token');
    this.roles = [];
    this.userName = '';
    this.tokenSubject.next(null); // 🔹 notifica logout
  }

  logoutWithMessage() {
    localStorage.removeItem('token');
    localStorage.setItem('logoutReason', 'Sua sessão expirou. Faça login novamente.');
    this.roles = [];
    this.userName = '';
    this.tokenSubject.next(null); // 🔹 notifica logout
  }

  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const decoded: any = jwtDecode(token);
      const exp = decoded.exp;
      const now = Math.floor(Date.now() / 1000);
      return exp > now;
    } catch (e) {
      return false;
    }
  }

  private decodeToken(token: string) {
    try {
      const decoded: any = jwtDecode(token);
      const role = decoded.role; // 🔹 pega claim único
      this.roles = role ? [`ROLE_${role}`] : []; // 🔹 monta lista
      this.userName = decoded.sub || '';
    } catch (e) {
      this.roles = [];
      this.userName = '';
    }
  }

  hasRole(role: string): boolean {
    return this.roles.includes(`ROLE_${role}`);
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isVendedor(): boolean {
    return this.hasRole('VENDEDOR');
  }

  isGerente(): boolean {
    return this.hasRole('GERENTE_SISTEMA');
  }

  getUserName(): string {
    return this.userName;
  }

  getUserRole(): string {

    return this.roles.length > 0 ? this.roles[0].replace('ROLE_', '') : '';
  }

  initializeAuth() {
    const token = this.getToken();
    if (token && this.isTokenValid()) {
      this.decodeToken(token);
      this.tokenSubject.next(token); // 🔹 garante que o token inicial seja emitido
    } else {
      this.logout();
    }
  }
  refreshToken(): Observable<string> {
  const refresh = localStorage.getItem('refreshToken');
  return this.http.post<{ token: string }>(`${this.apiUrl}/refresh`, { refresh })
    .pipe(
      tap(res => {
        this.saveToken(res.token);
      }),
      map(res => res.token)
    );
}

}
