# Architecture du Projet de Gestion de Bibliothèque

## Vue d'ensemble

Ce projet est une application de gestion de bibliothèque développée en Java, utilisant le framework JavaFX pour l'interface utilisateur et MySQL pour la persistance des données. L'application suit le modèle d'architecture **MVC (Model-View-Controller)** pour assurer une séparation claire des responsabilités et faciliter la maintenance du code.

## Technologies Utilisées

- **Java**: Langage de programmation principal
- **JavaFX**: Framework pour l'interface utilisateur graphique
- **MySQL**: Système de gestion de base de données relationnelle
- **Maven**: Outil de gestion et d'automatisation de production pour les projets Java

## Structure du Projet

```
Library_Project
│
├── src/main/java/com/example/library/
│   ├── Main.java                    # Point d'entrée de l'application
│   ├── controller/                  # Contrôleurs de l'application
│   │   ├── LibraryController.java   # Gestion des opérations principales
│   │   └── LoginController.java     # Gestion de l'authentification
│   │
│   ├── model/                       # Modèles de données
│   │   ├── Book.java                # Représente un livre
│   │   ├── Loan.java                # Représente un prêt
│   │   ├── Log.java                 # Représente une entrée de journal
│   │   └── User.java                # Représente un utilisateur
│   │
│   ├── dao/                         # Data Access Objects
│   │   ├── BookDAO.java             # Accès aux données des livres
│   │   ├── LoanDAO.java             # Accès aux données des prêts
│   │   ├── LogDAO.java              # Accès aux données des journaux
│   │   └── UserDAO.java             # Accès aux données des utilisateurs
│   │
│   └── util/                        # Utilitaires
│
├── resources/                       # Ressources de l'application (CSS, FXML, etc.)
│
└── SQL/                             # Scripts SQL pour la base de données
    ├── library_db.sql               # Création de la structure de la base de données
    ├── users_table.sql              # Définition de la table des utilisateurs
    └── update_users_table.sql       # Mises à jour de la table des utilisateurs
```

## Architecture MVC

### Modèle (Model)

Le modèle représente les données de l'application et la logique métier. Dans ce projet, les modèles sont:

- **Book**: Représente un livre avec ses attributs (titre, auteur, ISBN, statut, etc.)
- **Loan**: Représente un prêt avec ses attributs (livre emprunté, date de prêt, date de retour, etc.)
- **Log**: Représente une entrée dans le journal d'activité
- **User**: Représente un utilisateur du système avec ses informations d'authentification

### Vue (View)

La vue est responsable de l'affichage des données à l'utilisateur. Dans ce projet, les vues sont implémentées avec JavaFX:

- **Interface de connexion**: Formulaire de login pour l'authentification
- **Interface principale à onglets**:
  - Onglet "Liste des Livres": Affiche tous les livres disponibles
  - Onglet "Gestion des Livres": Interface pour ajouter, modifier et supprimer des livres
  - Onglet "Historique et Logs": Affiche l'historique des opérations effectuées

### Contrôleur (Controller)

Les contrôleurs gèrent les interactions entre le modèle et la vue:

- **LoginController**: Gère l'authentification des utilisateurs
- **LibraryController**: Gère toutes les opérations liées à la bibliothèque (ajout/modification/suppression de livres, gestion des prêts, etc.)

## Couche d'Accès aux Données (DAO)

La couche DAO (Data Access Object) sert d'intermédiaire entre l'application et la base de données:

- **BookDAO**: Gère les opérations CRUD pour les livres
- **LoanDAO**: Gère les opérations CRUD pour les prêts
- **LogDAO**: Gère les opérations d'enregistrement et de consultation des journaux d'activité
- **UserDAO**: Gère les opérations d'authentification et de gestion des utilisateurs

## Structure de la Base de Données

La base de données MySQL `library_db` contient les tables suivantes:

### Table `books`
- `id`: Identifiant unique du livre
- `title`: Titre du livre
- `author`: Auteur du livre
- `isbn`: ISBN du livre
- `publication_year`: Année de publication
- `status`: Statut du livre (Available/Loaned)
- Autres informations bibliographiques

### Table `users`
- `id`: Identifiant unique de l'utilisateur
- `username`: Nom d'utilisateur
- `password`: Mot de passe (hashé)
- `role`: Rôle de l'utilisateur (admin, librarian, etc.)

### Table `loans`
- `id`: Identifiant unique du prêt
- `book_id`: Référence au livre emprunté
- `borrower_name`: Nom de l'emprunteur
- `loan_date`: Date du prêt
- `due_date`: Date prévue de retour
- `return_date`: Date effective de retour (null si non retourné)
- `status`: Statut du prêt

### Table `logs`
- `id`: Identifiant unique du log
- `user_id`: Référence à l'utilisateur ayant effectué l'action
- `action`: Type d'action effectuée
- `details`: Détails de l'action
- `timestamp`: Date et heure de l'action

## Flux de Travail de l'Application

1. **Authentification**:
   - L'utilisateur se connecte via l'interface de login
   - Le système vérifie les informations d'authentification (identifiant: admin, mot de passe: admin123 par défaut)

2. **Interface Principale**:
   - Après authentification, l'utilisateur accède à l'interface principale à onglets

3. **Gestion des Livres**:
   - Consultation de la liste des livres
   - Ajout, modification et suppression de livres
   - Recherche et filtrage des livres par différents critères

4. **Gestion des Prêts**:
   - Enregistrement des prêts
   - Suivi des retours
   - Gestion des retards

5. **Suivi des Activités**:
   - Toutes les actions sont enregistrées dans le journal d'activité
   - Consultation des statistiques en temps réel

## Fonctionnalités Clés

- **Authentification sécurisée** pour accéder à l'application
- **Gestion complète des livres** (ajout, modification, suppression)
- **Système de prêt** avec suivi des échéances
- **Changement de statut** des livres (Available/Loaned)
- **Recherche multicritères** pour faciliter la recherche de livres
- **Journalisation des opérations** pour tracer toutes les actions effectuées
- **Statistiques en temps réel** sur l'état de la bibliothèque

## Points Forts de l'Architecture

1. **Modularité**: La séparation des composants selon le modèle MVC facilite la maintenance et l'évolution du code.

2. **Abstraction de la base de données**: L'utilisation des DAOs permet d'isoler la logique d'accès aux données du reste de l'application.

3. **Interface utilisateur intuitive**: L'organisation en onglets offre une expérience utilisateur claire et fonctionnelle.

4. **Système de journalisation**: Toutes les actions sont tracées, ce qui permet un suivi précis de l'activité.

5. **Sécurité**: Authentification requise pour accéder à l'application.

## Perspectives d'Évolution

Le projet pourrait évoluer avec les fonctionnalités suivantes:

- Ajout d'un système de réservation de livres
- Implémentation d'un système de notification (retards, réservations disponibles)
- Module de gestion des abonnés
- Intégration avec des services externes (comme des catalogues en ligne)
- Implémentation d'une version mobile ou web
