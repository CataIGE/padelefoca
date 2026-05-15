import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchCreer } from './match-creer';

describe('MatchCreer', () => {
  let component: MatchCreer;
  let fixture: ComponentFixture<MatchCreer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchCreer],
    }).compileComponents();

    fixture = TestBed.createComponent(MatchCreer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
