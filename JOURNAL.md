# Journal de développement — Padelefoca

## Contexte du projet
Application web de réservation de padel pour 3 clubs (Bruxelles, Gand, Namur).
- **Frontend** : Angular 21 + Angular Material + Space Grotesk / Inter
- **Backend** : Spring Boot 3.5+ (Java 21+)
- **Base de données** : SQL Server (Docker)
- **Repo GitHub** : https://github.com/CataIGE/padelefoca

---

## Thème visuel
- **Fond** : `#F5F5F0` (beige clair)
- **Bleu marine** : `#0D1B2A` (couleur principale)
- **Bleu** : `#1E6FD9` (accents, interface admin)
- **Jaune** : `#C8D400` (boutons joueur, interface joueur)
- **Polices** : Space Grotesk (titres) + Inter (corps)
- **Logo** : `logo.png` (phoque jouant au padel)
- **Mascotte admin** : `ImageAdmin.png` (phoque avec lunettes)
- **Image connexion joueur** : `ImageConnexion.png`

---

## Architecture Angular

src/app/
├── joueur/          → pages interface joueur
├── admin/           → pages interface admin
├── shared/          → composants réutilisables (navbar, home)
├── models/          → interfaces TypeScript
├── services/        → services HTTP
└── guards/          → guards de sécurité

### Choix techniques importants
- **Standalone components** (pas de NgModules)
- **Lazy loading** sur toutes les routes
- **Template Driven Forms** avec `[(ngModel)]`
- **Nouvelle syntaxe Angular** : `@if`, `@for` (pas `*ngIf`, `*ngFor`)
- **Variables CSS globales** dans `styles.css` avec `:root`
- **Responsive mobile** sur toutes les pages (`@media max-width: 768px`)

---

## Issues terminées

### Phase 1 (setup)
- ✅ #1 — Initialisation projet Angular
- ✅ #2 — Structure composants (navbar, header, footer, content)
- ✅ #3 — Angular Material
- ✅ #4 — Spring Boot init
- ✅ #5 — Routing Angular

### Phase 2 (fondations)
- ✅ #10 — Structure dossiers + routing complet avec lazy loading
- ✅ #11 — Homepage (hero, stats, cards joueur/admin)
- ✅ #12 — Page connexion joueur + navbar globale réutilisable
- ✅ #13 — Page inscription joueur (onglet dans connexion)
- ✅ #14 — Page connexion admin
- ✅ #15 — Guards (JoueurGuard, AdminGuard, AdminGlobalGuard)
- ✅ #16 — Modèles TypeScript (Joueur, Admin, Site, Match, Reservation, Paiement)
- ✅ #17 — Services Angular (Auth, Joueur, Site, Match, Reservation, Paiement, Admin, Statistique)

---

## Décisions importantes

### Matricule joueur
- Format : `L` (libre) / `S` (site) / `G` (global) + 4 chiffres
- **Généré par le backend** — pas le frontend
- Promotion automatique : L → S après 3 réservations sur même site, S → G après 6 réservations

### Authentification
- **Joueur** : matricule stocké en `sessionStorage`
- **Admin** : token JWT stocké en `sessionStorage` + rôle (`GLOBAL` ou `SITE`)
- **Intercepteur HTTP** : ajoute automatiquement les credentials à chaque requête

### Formulaires
- Téléphone **facultatif** dans l'inscription joueur
- Validation format matricule côté frontend : `/^[LSG]\d{4}$/`
- Connexion et inscription joueur sur la **même page** avec onglets

### Sécurité des routes
| Route | Guard |
|---|---|
| `/joueur/sites`, `/joueur/calendrier/:siteId`, `/joueur/match/creer/:siteId/:creneau`, `/joueur/profil` | JoueurGuard |
| `/admin/accueil`, `/admin/statistiques` | AdminGuard |
| `/admin/gestion` | AdminGlobalGuard |

---

## Phase 3 — À faire

### Pages joueur
- [ ] Page choix du site (`/joueur/sites`)
- [ ] Page calendrier (`/joueur/calendrier/:siteId`)
- [ ] Page création match (`/joueur/match/creer/:siteId/:creneau`)
- [ ] Page profil joueur (`/joueur/profil`)

### Pages admin
- [ ] Page accueil admin (`/admin/accueil`)
- [ ] Page statistiques (`/admin/statistiques`)
- [ ] Page gestion admins (`/admin/gestion`)

---

## URL importantes
- Frontend : http://localhost:4200
- Backend : http://localhost:8080
- Swagger : http://localhost:8080/swagger-ui/index.html