package com.appzoi.appzoi.repository;
import com.appzoi.appzoi.model.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface CalificacionVeterinarioRepositorio extends JpaRepository<CalificacionVeterinarioEntity,Integer>{
 @EntityGraph(attributePaths={"dueno","veterinario","veterinario.usuario"}) List<CalificacionVeterinarioEntity> findByVeterinarioOrderByActualizadaEnDesc(VeterinarioPerfilEntity v);
 Optional<CalificacionVeterinarioEntity> findByDuenoAndVeterinario(UsuarioEntity d,VeterinarioPerfilEntity v);
 long countByVeterinario(VeterinarioPerfilEntity v);
 @Query("select avg(c.estrellas) from CalificacionVeterinarioEntity c where c.veterinario=:v") Double promedio(@Param("v") VeterinarioPerfilEntity v);
 void deleteByDueno(UsuarioEntity d); void deleteByVeterinario(VeterinarioPerfilEntity v);
}
