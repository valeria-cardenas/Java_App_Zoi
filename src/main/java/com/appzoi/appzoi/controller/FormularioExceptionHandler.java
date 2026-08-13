package com.appzoi.appzoi.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

/** Convierte errores técnicos de formularios en alertas comprensibles del backend. */
@ControllerAdvice
public class FormularioExceptionHandler {

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            MultipartException.class
    })
    public String formularioInvalido(Exception exception, HttpServletRequest request,
            RedirectAttributes flash) {
        String atributo = atributoError(request.getRequestURI());
        String mensaje;
        if (exception instanceof MaxUploadSizeExceededException) {
            mensaje = "El archivo seleccionado supera el tamaño máximo permitido.";
        } else if (exception instanceof MissingServletRequestParameterException missing) {
            mensaje = "El campo «" + nombreCampo(missing.getParameterName()) + "» es obligatorio.";
        } else if (exception instanceof MethodArgumentTypeMismatchException mismatch) {
            mensaje = "El campo «" + nombreCampo(mismatch.getName()) + "» tiene un formato inválido.";
        } else {
            mensaje = "No fue posible procesar el archivo enviado. Revisa su formato y tamaño.";
        }
        flash.addFlashAttribute(atributo, mensaje);
        return "redirect:" + destinoSeguro(request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String restriccionDeDatos(DataIntegrityViolationException exception,
            HttpServletRequest request, RedirectAttributes flash) {
        flash.addFlashAttribute(atributoError(request.getRequestURI()),
                "No se pudo guardar la información porque ya existe un dato igual o no cumple las reglas requeridas.");
        return "redirect:" + destinoSeguro(request);
    }

    private String atributoError(String ruta) {
        if (ruta.startsWith("/registro")) return "errorRegistro";
        if (ruta.startsWith("/admin")) return "errorAdmin";
        if (ruta.contains("/contrasena")) return "errorPassword";
        if (ruta.equals("/perfil/eliminar")) return "errorEliminar";
        if (ruta.contains("/calificar")) return "errorCalificacion";
        return "errorPerfil";
    }

    private String destinoSeguro(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = URI.create(referer);
                if (uri.getHost() == null || uri.getHost().equalsIgnoreCase(request.getServerName())) {
                    String path = uri.getRawPath();
                    String query = uri.getRawQuery();
                    if (path != null && path.startsWith("/") && !path.startsWith("//")) {
                        return query == null ? path : path + "?" + query;
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // Si el encabezado es inválido se usa una ruta interna segura.
            }
        }
        return "/dashboard";
    }

    private String nombreCampo(String nombre) {
        if (nombre == null || nombre.isBlank()) return "dato solicitado";
        return switch (nombre) {
            case "confirmPassword", "confirmarPassword" -> "confirmación de contraseña";
            case "password", "passwordActual" -> "contraseña";
            case "passwordNueva" -> "nueva contraseña";
            case "fechaNacimiento" -> "fecha de nacimiento";
            case "fechaHora" -> "fecha y hora";
            case "mascotaId" -> "mascota";
            case "tipoPerfil" -> "tipo de perfil";
            case "tipoMascota" -> "tipo de mascota";
            case "tipoSangre" -> "tipo de sangre";
            case "estrellas" -> "calificación";
            default -> nombre.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
        };
    }
}
