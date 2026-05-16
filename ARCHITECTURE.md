# Architecture — Padelefoca

## Frontend

### Technologie
- **Framework** : Angular 21 (Standalone Components)
- **Langage** : TypeScript
- **UI** : Angular Material + CSS personnalisé
- **Polices** : Space Grotesk (titres), Inter (corps)
- **Gestion des formulaires** : Template Driven Forms (FormsModule)
- **Routing** : Angular Router avec Lazy Loading
- **HTTP** : HttpClient avec intercepteur JWT/Matricule
- **State management** : Signals Angular + toSignal()

### Architecture en couches
src/app/
├── shared/          → Composants réutilisables (navbar, home)
├── joueur/          → Composants interface joueur
├── admin/           → Composants interface admin
├── services/        → Services HTTP (auth, joueur, site, match...)
├── models/          → Interfaces TypeScript (Joueur, Match, Site...)
└── guards/          → Guards de sécurité (JoueurGuard, AdminGuard...)

### Routes principales
| Route | Composant | Protection |
|---|---|---|
| `/` | HomeComponent | Public |
| `/joueur/connexion` | ConnexionComponent | Public |
| `/joueur/sites` | SitesComponent | JoueurGuard |
| `/joueur/calendrier/:siteId` | CalendrierComponent | JoueurGuard |
| `/joueur/match/creer/:siteId/:creneau` | MatchCreerComponent | JoueurGuard |
| `/joueur/profil` | ProfilComponent | JoueurGuard |
| `/admin/connexion` | ConnexionAdminComponent | Public |
| `/admin/accueil` | AccueilComponent | AdminGuard |
| `/admin/statistiques` | StatistiquesComponent | AdminGuard |
| `/admin/gestion` | GestionComponent | AdminGlobalGuard |

### Librairies principales
| Librairie | Version | Usage |
|---|---|---|
| `@angular/core` | 21 | Framework principal |
| `@angular/material` | 21 | Composants UI |
| `@angular/router` | 21 | Routing et navigation |
| `@angular/forms` | 21 | Formulaires |
| `@angular/common/http` | 21 | Appels HTTP |
| `rxjs` | 7+ | Observables et toSignal |

---

## Backend

### Technologie
- **Framework** : Spring Boot 4.0.6
- **Langage** : Java 21
- **Base de données** : SQL Server 2025
- **ORM** : Spring Data JPA + Hibernate 7
- **Sécurité** : Spring Security 7 + JWT (jjwt 0.12.6)
- **Documentation API** : Swagger (springdoc-openapi 2.8.9)
- **Utilitaires** : Lombok 1.18

### Architecture en couches
be.ephec.backend/
├── controller/      → Endpoints REST (@RestController)
├── service/         → Logique métier (@Service)
├── repository/      → Accès données (@Repository)
├── model/           → Entités JPA (@Entity)
├── dto/             → Objets de transfert (Request/Response)
├── config/          → Configuration (Security, CORS, JWT)
└── scheduler/       → Tâches automatiques (@Scheduled)

### Couche Controller
Reçoit les requêtes HTTP, délègue au service, retourne la réponse. **Aucune logique métier.**

### Couche Service
Contient toute la logique métier. Valide les règles avant d'écrire en base.

### Couche Repository
Interfaces `JpaRepository` pour l'accès aux données SQL Server.

### Sécurité
- **Joueurs** : identifiés par matricule (`X1234`) envoyé dans le header `X-Matricule`
- **Admins** : authentifiés par email/mot de passe, token JWT dans le header `Authorization: Bearer <token>`
- **Intercepteur Angular** : ajoute automatiquement les credentials à chaque requête HTTP

### Librairies principales
| Librairie | Version | Usage |
|---|---|---|
| `spring-boot-starter-webmvc` | 4.0.6 | API REST |
| `spring-boot-starter-data-jpa` | 4.0.6 | ORM / accès DB |
| `spring-boot-starter-security` | 4.0.6 | Sécurité |
| `mssql-jdbc` | 13.2.1 | Driver SQL Server |
| `jjwt` | 0.12.6 | Génération/validation JWT |
| `lombok` | 1.18.46 | Réduction boilerplate |
| `springdoc-openapi` | 2.8.9 | Documentation Swagger |

### Documentation API (Swagger)
Une fois le backend démarré, l'API est documentée à l'adresse :
http://localhost:8080/swagger-ui.html
---

## Communication Frontend ↔ Backend
Angular (localhost:4200)
↓ HTTP REST
Spring Boot (localhost:8080/api/...)
↓ JPA
SQL Server (localhost:50247/padelefoca)

Le frontend appelle le backend via des services Angular.
Le backend expose une API REST documentée via Swagger.
La communication est sécurisée via JWT pour les admins et matricule pour les joueurs.