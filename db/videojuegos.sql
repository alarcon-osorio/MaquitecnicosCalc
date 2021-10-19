-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 27-08-2020 a las 21:34:38
-- Versión del servidor: 10.4.14-MariaDB
-- Versión de PHP: 7.4.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `videojuegos`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `distribuidor`
--

CREATE TABLE `distribuidor` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) NOT NULL,
  `sitio_web` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `distribuidor`
--

INSERT INTO `distribuidor` (`id`, `nombre`, `sitio_web`) VALUES
(1, 'Proveedor 1', 'Sitio_web1.com'),
(2, 'Proveedor 2', 'Sitio_web1.com'),
(3, 'Proveedor 3', 'Sitio_web1.com'),
(4, 'Proveedor 4', 'Sitio_web1.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `videojuego`
--

CREATE TABLE `videojuego` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) NOT NULL,
  `descripcion` varchar(3000) DEFAULT NULL,
  `imagen_url` varchar(500) DEFAULT NULL,
  `distribuidor_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `videojuego`
--

INSERT INTO `videojuego` (`id`, `nombre`, `descripcion`, `imagen_url`, `distribuidor_id`) VALUES
(1, 'Game 1', 'Desc Game 1', 'https://lorenashleigh.files.wordpress.com/2013/06/big-mario-super-mario-bros-32901984-1586-2462.png', 2),
(2, 'Game 2', 'Desc Game 2', 'http://vignette1.wikia.nocookie.net/fantendo/images/4/4d/Small_Mario_NSMBU.png/revision/latest?cb=20121229043401', 2),
(3, 'Game 3', 'Desc Game 3', 'https://vignette2.wikia.nocookie.net/mario/images/1/1b/UMario.jpg/revision/latest?cb=20121030200758', 2),
(4, 'Game 4', 'Desc Game 4', 'http://www.geeksraisinggeeks.com/wp-content/uploads/2013/08/Mario-Animals.jpg', 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `distribuidor`
--
ALTER TABLE `distribuidor`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `videojuego`
--
ALTER TABLE `videojuego`
  ADD PRIMARY KEY (`id`),
  ADD KEY `distribuidor_id` (`distribuidor_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `distribuidor`
--
ALTER TABLE `distribuidor`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `videojuego`
--
ALTER TABLE `videojuego`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `videojuego`
--
ALTER TABLE `videojuego`
  ADD CONSTRAINT `videojuego_ibfk_1` FOREIGN KEY (`distribuidor_id`) REFERENCES `distribuidor` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
