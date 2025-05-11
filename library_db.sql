-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : dim. 13 avr. 2025 à 15:32
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `library_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `books`
--

CREATE TABLE `books` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `isbn` varchar(255) NOT NULL,
  `status` varchar(50) NOT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `edition` varchar(255) DEFAULT NULL,
  `loanDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `books`
--

INSERT INTO `books` (`id`, `title`, `author`, `isbn`, `status`, `genre`, `edition`, `loanDate`) VALUES
(1, 'Harry Potter', 'JK Potter', 'ISBN1737119838243', 'Available', NULL, NULL, NULL),
(3, 'La femme de ménage qui voit tout', 'Freida McFadden', 'ISBN1737123661240', 'Loaned', 'Roman', 'City', '2025-04-07'),
(4, 'hh', 'hh', 'ISBN1738338857021', 'Loaned', 'hh', 'hh', '2025-04-07'),
(5, '.21', '21', 'ISBN1738338874985', 'Loaned', '12', '12', '2025-04-06'),
(6, 'klkl', 'klklk', 'ISBN1738338891687', 'Loaned', 'lklklk', ',kl,klk,', '2025-04-06'),
(8, 'olo', 'ollk', 'ISBN1739442933563', 'Loaned', 'olo', 'mlo', '2025-02-21'),
(9, 'zaz', 'zae', 'ISBN1743933570310', 'Loaned', 'aze', 'aezae', '2025-04-07'),
(10, 'Le Petit Prince', 'Antoine de Saint-Exupéry', '9782070360024', 'disponible', 'Conte', '1ère édition', NULL),
(11, 'Les Misérables', 'Victor Hugo', '9782070360017', 'emprunté', 'Roman historique', 'édition originale', NULL),
(12, 'L\'Étranger', 'Albert Camus', '9782070360031', 'disponible', 'Philosophique', 'Nouvelle édition', NULL),
(13, 'Madame Bovary', 'Gustave Flaubert', '9782070360048', 'disponible', 'Roman réaliste', '2ème édition', NULL),
(14, 'Le Rouge et le Noir', 'Stendhal', '9782070360055', 'emprunté', 'Roman', 'édition annotée', NULL),
(15, 'Notre-Dame de Paris', 'Victor Hugo', '9782070360062', 'disponible', 'Roman historique', 'édition de luxe', NULL),
(16, 'Candide', 'Voltaire', '9782070360079', 'disponible', 'Philosophique', 'édition critique', NULL),
(17, 'La Peste', 'Albert Camus', '9782070360086', 'emprunté', 'Allégorie', 'édition révisée', NULL),
(18, 'Germinal', 'Émile Zola', '9782070360093', 'disponible', 'Roman naturaliste', 'édition classique', NULL),
(19, 'Le Comte de Monte-Cristo', 'Alexandre Dumas', '9782070360109', 'disponible', 'Aventure', 'édition illustrée', NULL),
(20, 'Bel-Ami', 'Guy de Maupassant', '9782070360116', 'emprunté', 'Roman', 'édition spéciale', NULL),
(21, 'La Chartreuse de Parme', 'Stendhal', '9782070360123', 'disponible', 'Roman historique', 'édition revue', NULL),
(22, 'Voyage au centre de la Terre', 'Jules Verne', '9782070360130', 'disponible', 'Science-fiction', '1ère édition', NULL),
(23, 'Twenty Thousand Leagues Under the Seas', 'Jules Verne', '9782070360147', 'emprunté', 'Aventure', 'édition bilingue', NULL),
(24, 'Around the World in 80 Days', 'Jules Verne', '9782070360154', 'disponible', 'Roman d’aventure', 'édition classique', NULL),
(25, 'La Princesse de Clèves', 'Madame de La Fayette', '9782070360161', 'disponible', 'Roman', 'édition commentée', NULL),
(26, 'L\'Assommoir', 'Émile Zola', '9782070360178', 'emprunté', 'Roman naturaliste', 'édition revue', NULL),
(27, 'Manon Lescaut', 'Abbé Prévost', '9782070360185', 'disponible', 'Roman d’amour', 'édition originale', NULL),
(28, 'Les Fleurs du mal', 'Charles Baudelaire', '9782070360192', 'disponible', 'Poésie', 'édition annotée', NULL),
(29, 'La Condition humaine', 'André Malraux', '9782070360208', 'emprunté', 'Roman philosophique', 'édition spéciale', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `loans`
--

CREATE TABLE `loans` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `loan_date` date NOT NULL,
  `due_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `loans`
--

INSERT INTO `loans` (`id`, `user_id`, `book_id`, `loan_date`, `due_date`) VALUES
(4, 3, 1, '2025-04-07', '2025-04-21'),
(7, 5, 4, '2025-04-07', '2025-04-21'),
(8, 6, 3, '2025-04-07', '2025-04-21');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `name`, `email`) VALUES
(1, 'dd', 'dd@dd.com'),
(2, 'dd', 'dd@dd.com'),
(3, 'david MENDES', 'david mendes@example.com'),
(4, 'david', 'david@example.com'),
(5, 'david MENDEVS', 'david mendevs@example.com'),
(6, 'DADA', 'dada@example.com');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `isbn` (`isbn`);

--
-- Index pour la table `loans`
--
ALTER TABLE `loans`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `book_id` (`book_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `books`
--
ALTER TABLE `books`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT pour la table `loans`
--
ALTER TABLE `loans`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `loans`
--
ALTER TABLE `loans`
  ADD CONSTRAINT `loans_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `loans_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
