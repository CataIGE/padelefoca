import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './composants/navbar/navbar';
import { Header } from './composants/header/header';
import { Footer } from './composants/footer/footer';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
}