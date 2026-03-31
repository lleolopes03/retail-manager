import { Component } from '@angular/core';
import { WhatsappService } from '../whatsapp.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-recibo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './recibo.component.html',
  styleUrls: ['./recibo.component.scss']
})
export class ReciboComponent {
  statusMessage = '';

  constructor(private whatsappService: WhatsappService) {}

  enviarRecibo() {
    const clienteId = '123'; // depois substitui pelo ID real
    this.whatsappService.enviarRecibo(clienteId).subscribe({
      next: () => this.statusMessage = 'Recibo enviado com sucesso!',
      error: () => this.statusMessage = 'Erro ao enviar recibo.'
    });
  }
}
