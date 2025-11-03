import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { TicketListComponent } from './components/ticket-list/ticket-list.component';
import { TicketFormComponent } from './components/ticket-form/ticket-form.component';
import { SpecialOperationsComponent } from './components/special-operations/special-operations.component';
import { AdminPanelComponent } from './components/admin-panel/admin-panel.component';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/tickets', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'tickets',
    component: TicketListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'tickets/new',
    component: TicketFormComponent,
    canActivate: [authGuard]
  },
  {
    path: 'tickets/:id',
    component: TicketFormComponent,
    canActivate: [authGuard]
  },
  {
    path: 'tickets/:id/edit',
    component: TicketFormComponent,
    canActivate: [authGuard]
  },
  {
    path: 'special-operations',
    component: SpecialOperationsComponent,
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    component: AdminPanelComponent,
    canActivate: [authGuard, adminGuard]
  },
  { path: '**', redirectTo: '/tickets' }
];
