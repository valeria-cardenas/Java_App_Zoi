package com.appzoi.appzoi.controller;
import com.appzoi.appzoi.model.*; import com.appzoi.appzoi.repository.*;
import java.security.Principal; import org.springframework.stereotype.Controller; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller public class CalificacionController {
 private final UsuarioRepositorio usuarios; private final VeterinarioPerfilRepositorio veterinarios; private final CalificacionVeterinarioRepositorio calificaciones; private final ConversacionRepositorio conversaciones; private final MensajeRepositorio mensajes;
 public CalificacionController(UsuarioRepositorio u,VeterinarioPerfilRepositorio v,CalificacionVeterinarioRepositorio c,ConversacionRepositorio conversaciones,MensajeRepositorio mensajes){usuarios=u;veterinarios=v;calificaciones=c;this.conversaciones=conversaciones;this.mensajes=mensajes;}
 @PostMapping("/dueno/veterinarios/{id}/calificar")
 public String calificar(@PathVariable Integer id,@RequestParam Integer estrellas,@RequestParam String comentario,Principal principal,RedirectAttributes flash){
  UsuarioEntity dueno=usuarios.findByEmail(principal.getName()).orElseThrow(); VeterinarioPerfilEntity vet=veterinarios.findById(id).orElse(null); String texto=comentario==null?"":comentario.trim();
  if(vet==null||!conversaciones.existsByDuenoAndVeterinario(dueno,vet)||!mensajes.existsByConversacion_DuenoAndConversacion_VeterinarioAndAutor(dueno,vet,vet.getUsuario())){flash.addFlashAttribute("errorCalificacion","Podrás calificar cuando el veterinario haya respondido tu consulta.");return "redirect:/dueno/veterinarios/"+id;}
  if(estrellas==null||estrellas<1||estrellas>5||texto.length()<3||texto.length()>800){flash.addFlashAttribute("errorCalificacion","Selecciona de 1 a 5 estrellas y escribe un comentario de 3 a 800 caracteres.");return "redirect:/dueno/veterinarios/"+id;}
  CalificacionVeterinarioEntity c=calificaciones.findByDuenoAndVeterinario(dueno,vet).orElseGet(CalificacionVeterinarioEntity::new); c.setDueno(dueno);c.setVeterinario(vet);c.setEstrellas(estrellas);c.setComentario(texto);calificaciones.save(c);
  flash.addFlashAttribute("successCalificacion","Tu calificación fue guardada."); return "redirect:/dueno/veterinarios/"+id;
 }
}
