import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators, FormControl, FormGroup } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService, LoginCredentials } from '../auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    HttpClientModule,
    MatSnackBarModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup<{
  login: FormControl<string>;
  senha: FormControl<string>;
}>;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.nonNullable.group({
  login: ['', Validators.required],
  senha: ['', Validators.required]
});
  }

  ngOnInit() {
    // 🔹 Exibe mensagem se a sessão expirou
    const reason = localStorage.getItem('logoutReason');
    if (reason) {
      this.snackBar.open(reason, 'Fechar', { duration: 4000 });
      localStorage.removeItem('logoutReason');
    }
  }

  onSubmit() {
  if (this.loginForm.valid) {
    const credentials = this.loginForm.getRawValue() as LoginCredentials;
    this.authService.login(credentials).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Erro no login:', err);
        this.snackBar.open('Login ou senha inválidos', 'Fechar', { duration: 3000 });
      }
    });
  }
}

}
