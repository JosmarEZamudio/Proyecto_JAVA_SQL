package com.example;

import java.io.IOException;
import java.nio.file.*;

public class RutasImagenes {

    private static final Path CARPETA_USUARIO = Paths.get(
        System.getenv("LOCALAPPDATA"),
        "GestionRestaurante",
        "datos",
        "imagenes"
    );

    // Carpeta de imágenes iniciales incluida en la distribución
    private static Path obtenerCarpetaInicial() {
        try {
            Path jar = Paths.get(
                Main.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()
            );

            return jar.resolveSibling("datos").resolve("imagenes");
        } catch (Exception e) {
            throw new RuntimeException(
                "No se pudo localizar la carpeta de imágenes iniciales", e
            );
        }
    }

    // Copia las imágenes iniciales sin sobrescribir las existentes
    public static void inicializar() throws IOException {
        Path iniciales = obtenerCarpetaInicial();

        if (!Files.exists(iniciales)) {
            return;
        }

        try (var archivos = Files.walk(iniciales)) {
            archivos.filter(Files::isRegularFile).forEach(origen -> {
                try {
                    Path relativo = iniciales.relativize(origen);
                    Path destino = CARPETA_USUARIO.resolve(relativo);

                    Files.createDirectories(destino.getParent());

                    if (Files.notExists(destino)) {
                        Files.copy(origen, destino);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static Path obtenerImagen(int idRestaurante, int idProducto)
            throws IOException {

        Path carpeta = CARPETA_USUARIO.resolve(
            String.valueOf(idRestaurante)
        );

        for (String extension : new String[]{".jpg", ".jpeg", ".png"}) {
            Path imagen = carpeta.resolve(idProducto + extension);

            if (Files.exists(imagen)) {
                return imagen;
            }
        }

        return null;
    }

    public static Path obtenerCarpetaRestaurante(int idRestaurante)
            throws IOException {

        Path carpeta = CARPETA_USUARIO.resolve(
            String.valueOf(idRestaurante)
        );

        Files.createDirectories(carpeta);
        return carpeta;
    }
}