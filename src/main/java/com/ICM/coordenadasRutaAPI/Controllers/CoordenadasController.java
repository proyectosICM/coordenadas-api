package com.ICM.coordenadasRutaAPI.Controllers;

import com.ICM.coordenadasRutaAPI.Models.CoordenadasModel;
import com.ICM.coordenadasRutaAPI.Models.PaisesModel;
import com.ICM.coordenadasRutaAPI.Models.RutasModel;
import com.ICM.coordenadasRutaAPI.RequestData.CoordenadasDTO;
import com.ICM.coordenadasRutaAPI.Services.CoordenadasService;
import com.ICM.coordenadasRutaAPI.Services.PaisesService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/coordenadas")
public class CoordenadasController {
    @Autowired

    CoordenadasService coordenadasService;

    @GetMapping("/ruta/{ruta}")
    public List<CoordenadasModel> GetxCoordenadas(@PathVariable Long ruta) {
        return coordenadasService.GetxRutas(ruta);
    }

    /* Para GIAN */
    @GetMapping("/rutadis/{codigo}")
    public List<CoordenadasModel> GetxCoordenadasxDisp(@PathVariable String codigo) {
        return coordenadasService.GetCoordenadasxDisp(codigo);
    }

    @GetMapping("/rutadisid/{codigo}")
    public List<CoordenadasModel> GetxCoordenadasxDispId(@PathVariable Long codigo) {
        return coordenadasService.GetCoordenadasxDispID(codigo);
    }

    @GetMapping("/download/{id}")
    public void downloadCoordenadas(@PathVariable Long id, HttpServletResponse response) {
        coordenadasService.GetCoordenadasxDispIDAndDownload(id, response);
    }

    @GetMapping("/dto/{id}")
    public List<CoordenadasDTO> getCoordenadasDTO(@PathVariable Long id) {
        return coordenadasService.GetCoordenadasxDispIDDTO(id);
    }
    /* */

    @GetMapping("/cxr/{ruta}")
    public Page<CoordenadasModel> obtenerCoordenadasPaginadas(
            @PathVariable Long ruta,
            @RequestParam(defaultValue = "1")  int pageNumber,
            @RequestParam(defaultValue = "4") int pageSize
    ) {
        return coordenadasService.GetxRutasP(ruta, pageNumber, pageSize);
    }
    /*  http download chunk */
    @GetMapping("/descargarCoordenadas/{rutaid}")
    public ResponseEntity<InputStreamResource> descargarCoordenadas(@PathVariable Long rutaid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "coordenadas.txt");

        InputStream inputStream = coordenadasService.generarArchivosTxt(rutaid);

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }
    /* */
    // CRUD

    @GetMapping
    public List<CoordenadasModel> GetAll (){
        return coordenadasService.Get();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordenadasModel> GetById(@PathVariable Long id){
        Optional<CoordenadasModel> paises = coordenadasService.GetById(id);
        return new ResponseEntity<>(paises.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CoordenadasModel> Save(@RequestBody CoordenadasModel coordenadasModel){
        CoordenadasModel ccoordenadas = coordenadasService.Save(coordenadasModel);
        return new ResponseEntity<>(ccoordenadas, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordenadasModel> Edit(@PathVariable Long id, @RequestBody CoordenadasModel coordenadasModel){
        CoordenadasModel ecoordenada = coordenadasService.Edit(id, coordenadasModel);
        if (ecoordenada!=null){
            return new ResponseEntity<>(ecoordenada, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CoordenadasModel> Delete(@PathVariable Long id){
        coordenadasService.Delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
