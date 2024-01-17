-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3307
-- Généré le : jeu. 28 sep. 2023 à 08:32
-- Version du serveur : 10.6.5-MariaDB
-- Version de PHP : 7.4.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `vols`
--

-- --------------------------------------------------------

--
-- Structure de la table `livres`
--

DROP TABLE IF EXISTS `livres`;
CREATE TABLE IF NOT EXISTS `livres` (
  `ISBN` varchar(255) NOT NULL,
  `Titre` varchar(255) NOT NULL,
  `Auteur` varchar(255) NOT NULL,
  PRIMARY KEY (`ISBN`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `livres`
--

INSERT INTO `livres` (`ISBN`, `Titre`, `Auteur`) VALUES
('1111', 'Les Miserables', 'Victor Hugo');

-- --------------------------------------------------------

--
-- Structure de la table `vol`
--

DROP TABLE IF EXISTS `vol`;
CREATE TABLE IF NOT EXISTS `vol` (
  `Numvol` varchar(255) NOT NULL,
  `Heure_depart` time(6) NOT NULL,
  `Heure_arrive` time(6) NOT NULL,
  `Ville_depart` varchar(255) NOT NULL,
  `Ville_arrivee` varchar(255) NOT NULL,
  PRIMARY KEY (`Numvol`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `vol`
--

INSERT INTO `vol` (`Numvol`, `Heure_depart`, `Heure_arrive`, `Ville_depart`, `Ville_arrivee`) VALUES
('AF212', '09:21:00.000000', '14:10:00.000000', 'Paris', 'Moscow'),
('AF178', '12:56:00.000000', '14:15:00.000000', 'Paris', 'London'),
('TA215', '08:00:00.000000', '10:10:00.000000', 'Tunis', 'Paris'),
('OA005', '14:20:00.000000', '17:00:00.000000', 'Athens', 'Paris'),
('SA854', '22:00:00.000000', '10:14:00.000000', 'Singapore', 'Athens'),
('AA111', '15:45:00.000000', '21:10:00.000000', 'Beijing', 'Singapore'),
('AF218', '21:12:00.000000', '09:16:00.000000', 'Beijing', 'Paris'),
('SA012', '07:57:00.000000', '11:26:00.000000', 'Sydney', 'Singapore'),
('F23446', '12:30:00.000000', '15:15:00.000000', 'Paris Orly', 'Moscow'),
('A270', '12:00:00.000000', '18:00:00.000000', 'Paris', 'Madrid'),
('R345', '00:00:00.000000', '00:01:00.000000', 'Paris', 'Paris'),
('E546', '00:00:00.000000', '00:01:00.000000', 'Paris', 'Paris');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
