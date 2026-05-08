import { Routes } from '@angular/router';
import { Content } from './composants/content/content';

export const routes: Routes = [
  { path: '', component: Content },
  { path: '**', redirectTo: '' }
];