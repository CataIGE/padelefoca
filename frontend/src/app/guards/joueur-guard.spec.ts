import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { joueurGuard } from './joueur-guard';

describe('joueurGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => joueurGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
