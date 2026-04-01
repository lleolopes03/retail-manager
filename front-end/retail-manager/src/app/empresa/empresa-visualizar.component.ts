import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { NgIf, AsyncPipe } from '@angular/common';
import { EmpresaService } from './empresa.service';
import { Observable } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-empresa-visualizar',
  standalone: true,
  imports: [
    MatCardModule, 
    MatListModule, 
    NgIf, 
    AsyncPipe,
    MatIconModule,
    MatButtonModule,
    RouterModule
  ],
  templateUrl: './empresa-visualizar.component.html',
  styleUrls: ['./empresa-visualizar.component.scss']
})
export class EmpresaVisualizarComponent {
  empresa$: Observable<any>;

  constructor(private empresaService: EmpresaService,private router: Router, ) {
    this.empresa$ = this.empresaService.buscarEmpresa();
  }
  voltar() {
  this.router.navigate(['/dashboard']); // ou ['/dashboard'] se preferir
  }
}
