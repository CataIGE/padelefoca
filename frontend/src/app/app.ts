import { Component } from '@angular/core';
import { Navbar } from './composants/navbar/navbar';
import { Header } from './composants/header/header';
import { Content } from './composants/content/content';
import { Footer } from './composants/footer/footer';

@Component({
  selector: 'app-root',
  imports: [Navbar, Header, Content, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
}