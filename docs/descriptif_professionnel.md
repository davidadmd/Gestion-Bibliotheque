# Descriptif de la Réalisation Professionnelle : Système de Gestion de Bibliothèque

## 1. Présentation du Projet

### 1.1 Contexte
Le projet consiste en un système de gestion de bibliothèque développé en Java avec JavaFX pour l'interface utilisateur. Il permet la gestion complète des livres, des utilisateurs et des emprunts dans une bibliothèque.

### 1.2 Technologies Utilisées
- **Backend** : Java
- **Frontend** : JavaFX
- **Base de données** : MySQL
- **Architecture** : Modèle-Vue-Contrôleur (MVC)
- **Gestion de dépendances** : Maven

## 2. Architecture du Projet

### 2.1 Structure du Projet
```
Project-Library/
├── src/main/
│   ├── java/
│   │   ├── com.example.library/
│   │   │   ├── controller/
│   │   │   ├── dao/
│   │   │   ├── model/
│   │   │   └── util/
│   ├── resources/
│   │   └── com.example.library/
│   │       ├── views/
│   │       └── styles/
└── docs/
    └── diagrammes/
```

### 2.2 Composants Principaux

#### Modèles (Models)
- **Book** : Gestion des informations des livres
- **User** : Gestion des utilisateurs
- **Loan** : Gestion des emprunts

#### Contrôleurs (Controllers)
- **LibraryController** : Gestion des opérations sur les livres
- **UserManagementController** : Gestion des utilisateurs
- **LoanController** : Gestion des emprunts et retours

#### DAO (Data Access Objects)
- **BookDAO** : Accès aux données des livres
- **UserDAO** : Accès aux données des utilisateurs
- **LoanDAO** : Accès aux données des emprunts

## 3. Base de Données

### 3.1 Structure
La base de données comprend trois tables principales :

#### Table Books
```sql
CREATE TABLE books (
  id int(11) NOT NULL,
  title varchar(255) NOT NULL,
  author varchar(255) NOT NULL,
  isbn varchar(255) NOT NULL,
  status varchar(50) NOT NULL,
  genre varchar(255) DEFAULT NULL,
  edition varchar(255) DEFAULT NULL,
  loanDate date DEFAULT NULL
)
```

#### Table Users
```sql
CREATE TABLE users (
  id int(11) NOT NULL,
  name varchar(255) NOT NULL,
  email varchar(255) NOT NULL
)
```

#### Table Loans
```sql
CREATE TABLE loans (
  id int(11) NOT NULL,
  user_id int(11) NOT NULL,
  book_id int(11) NOT NULL,
  loan_date date NOT NULL,
  due_date date NOT NULL
)
```

### 3.2 Relations
- Un livre peut être emprunté par un seul utilisateur à la fois
- Un utilisateur peut emprunter plusieurs livres
- Chaque emprunt est lié à un livre et un utilisateur

## 4. Fonctionnalités Principales

### 4.1 Gestion des Livres
- Ajout de nouveaux livres
- Recherche de livres
- Mise à jour des informations
- Suivi du statut (disponible/emprunté)

### 4.2 Gestion des Utilisateurs
- Inscription des utilisateurs
- Gestion des profils
- Suivi des emprunts

### 4.3 Gestion des Emprunts
- Enregistrement des emprunts
- Suivi des dates de retour
- Gestion des retours

## 5. Interface Utilisateur

L'interface utilisateur est développée avec JavaFX et comprend :
- Une vue principale pour la gestion des livres
- Une vue pour la gestion des utilisateurs
- Une vue pour la gestion des emprunts
- Des formulaires pour les différentes opérations

## 6. Sécurité et Performance

### 6.1 Sécurité
- Validation des données en entrée
- Gestion des erreurs
- Contraintes d'intégrité dans la base de données

### 6.2 Performance
- Optimisation des requêtes SQL
- Indexation appropriée des tables
- Gestion efficace des connexions à la base de données

## 7. Perspectives d'Évolution

- Implémentation d'un système de réservation
- Ajout d'un système de notifications
- Intégration d'un système de statistiques
- Extension des fonctionnalités de recherche

## 8. Schémas Explicatifs

Les schémas UML suivants sont disponibles dans le dossier docs/ :
- Diagramme de séquence
- Diagramme de la base de données
- Modèle Conceptuel de Données (MCD)

Ces schémas illustrent l'architecture et les interactions entre les différents composants du système.
