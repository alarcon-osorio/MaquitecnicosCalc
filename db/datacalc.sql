-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 04-01-2022 a las 21:41:55
-- Versión del servidor: 10.5.12-MariaDB-cll-lve
-- Versión de PHP: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `u644237725_mtcalc`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `datacalc`
--

CREATE TABLE `datacalc` (
  `id` int(11) NOT NULL,
  `iddatastatic` int(11) NOT NULL,
  `value_cop` int(100) NOT NULL,
  `legalization` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `datacalc`
--

INSERT INTO `datacalc` (`id`, `iddatastatic`, `value_cop`, `legalization`) VALUES
(1, 1, 3800, '20'),
(2, 2, 3800, '25'),
(3, 3, 3800, '210');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `datacalc`
--
ALTER TABLE `datacalc`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `iddatastatic` (`iddatastatic`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `datacalc`
--
ALTER TABLE `datacalc`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
