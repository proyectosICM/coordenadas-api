package com.ICM.coordenadasRutaAPI.Services;

import com.ICM.coordenadasRutaAPI.Models.CoordenadasModel;
import com.ICM.coordenadasRutaAPI.Models.DispositivosModel;
import com.ICM.coordenadasRutaAPI.Models.RutasModel;
import com.ICM.coordenadasRutaAPI.Repositories.CoordenadasRepository;
import com.ICM.coordenadasRutaAPI.Repositories.DispositivosRepository;
import com.ICM.coordenadasRutaAPI.RequestData.CoordenadasDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoordenadasService {
    @Autowired
    CoordenadasRepository coordenadasRepository;
    @Autowired
    private DispositivosRepository dispositivosRepository;


    public List<CoordenadasModel> GetxRutas(Long ruta){
        RutasModel rutasModel = new RutasModel();
        rutasModel.setId(ruta);
        return coordenadasRepository.findByRutasModel(rutasModel);
    }

    /* Para GIAN */
    public List<CoordenadasModel> GetCoordenadasxDisp(String codigodis){
        Optional<DispositivosModel> dispositivo = dispositivosRepository.findByNombre(codigodis);
        Long rutaid = dispositivo.get().getRutasModel().getId();
        return coordenadasRepository.findByRutasModelId(rutaid);
    }

    public List<CoordenadasModel> GetCoordenadasxDispID(Long id){
        Optional<DispositivosModel> dispositivo = dispositivosRepository.findById(id);
        Long rutaid = dispositivo.get().getRutasModel().getId();
        return coordenadasRepository.findByRutasModelId(rutaid);
    }

    public void GetCoordenadasxDispIDAndDownload(Long id, HttpServletResponse response) {
        Optional<DispositivosModel> dispositivo = dispositivosRepository.findById(id);
        Long rutaid = dispositivo.get().getRutasModel().getId();
        List<CoordenadasModel> coordenadas = coordenadasRepository.findByRutasModelId(rutaid);

        try {
            // Configurar la respuesta para descargar un archivo
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=coordenadas.txt");

            // Escribir el contenido en el archivo de texto
            PrintWriter writer = response.getWriter();
            for (CoordenadasModel coordenada : coordenadas) {
                writer.println(coordenada.getCoordenadas() + ", " + coordenada.getRadio() + ", " + coordenada.getSonidosVelocidadModel().getNombre()
                        + ", " + coordenada.getSonidosVelocidadModel().getCodvel() + ", " + coordenada.getSonidosGeocercaModel().getCodsonido());
            }
            writer.flush();
        } catch (Exception e) {
            // Manejo de excepciones
            e.printStackTrace();
        }
    }

    public List<CoordenadasDTO> GetCoordenadasxDispIDDTO(Long id){
        Optional<DispositivosModel> dispositivo = dispositivosRepository.findById(id);
        Long rutaid = dispositivo.get().getRutasModel().getId();
        List<CoordenadasModel> coordenadas = coordenadasRepository.findByRutasModelId(rutaid);

        // Mapear CoordenadasModel a CoordenadasDTO
        return coordenadas.stream().map(coordenada -> {
            CoordenadasDTO dto = new CoordenadasDTO();
            dto.setCoordenadas(coordenada.getCoordenadas());
            dto.setRadio(coordenada.getRadio());
            dto.setNombreSonidoVelocidad(coordenada.getSonidosVelocidadModel().getNombre());
            dto.setCodvel(coordenada.getSonidosVelocidadModel().getCodvel());
            dto.setCodsonido(coordenada.getSonidosGeocercaModel().getCodsonido());
            return dto;
        }).collect(Collectors.toList());
    }

    /*  http download chunk */
    public InputStream generarArchivosTxt(Long rutaid) {
        List<CoordenadasModel> coordenadas = coordenadasRepository.findByRutasModelId(rutaid);
        StringBuilder data = new StringBuilder();

        for (CoordenadasModel coordenada : coordenadas) {
            // Lógica para agregar las coordenadas al StringBuilder
            data.append(coordenada.getCoordenadas() + ", " + coordenada.getRadio() + ", " + coordenada.getSonidosVelocidadModel().getNombre() + ", "
                    + coordenada.getSonidosVelocidadModel().getCodvel() + ", " + coordenada.getSonidosGeocercaModel().getCodsonido()).append("\n");

            // Si la longitud del contenido supera los 256 bytes, genera un archivo y reinicia el contenido
            if (data.toString().getBytes().length > 200) {
                try {
                    byte[] byteArray = data.toString().getBytes();
                    return new ByteArrayInputStream(byteArray);
                } finally {
                    data = new StringBuilder();
                }
            }
        }

        // Enviar el resto de las coordenadas
        if (data.length() > 0) {
            return new ByteArrayInputStream(data.toString().getBytes());
        }

        return null;
    }
    /* */


    public Page<CoordenadasModel> GetxRutasP(Long ruta, int pageNumber, int defaultPageSize) {
        RutasModel rutasModel = new RutasModel();
        rutasModel.setId(ruta);

        // Si defaultPageSize es menor o igual a 0, se utilizará un tamaño de página predeterminado
        //int pageSize = defaultPageSize <= 0 ? 10 : defaultPageSize;

        PageRequest pageRequest = PageRequest.of(pageNumber, 4);
        return coordenadasRepository.findByRutasModel(rutasModel, pageRequest);
    }


    //CRUD

    public List<CoordenadasModel> Get(){
        return coordenadasRepository.findAll();
    }

    public Optional<CoordenadasModel> GetById(Long id){
        return coordenadasRepository.findById(id);
    }

    public CoordenadasModel Save(CoordenadasModel coordenadasModel) {
        String[] coordenadas = coordenadasModel.getCoordenadas().split(", ");

        // Suponiendo que siempre tienes el formato latitud, longitud
        String latitud = coordenadas[0].replace(',', '.');
        String longitud = coordenadas[1].replace(',', '.');

        // Redondear las coordenadas a 7 decimales después de la coma
        latitud = String.format(Locale.US, "%.7f", Double.parseDouble(latitud));
        longitud = String.format(Locale.US, "%.7f", Double.parseDouble(longitud));

        // Volver a armar la cadena de coordenadas
        String coordenadaFormateada = latitud + ", " + longitud;

        // Actualizar la coordenada en el modelo antes de guardar
        coordenadasModel.setCoordenadas(coordenadaFormateada);
        return coordenadasRepository.save(coordenadasModel);
    }

    public CoordenadasModel Edit(Long id, CoordenadasModel coordenadasModel) {
        Optional<CoordenadasModel> existing = coordenadasRepository.findById(id);
        if(existing.isPresent()){
            CoordenadasModel coordenadas = existing.get();

            // Redondear las coordenadas al formato adecuado
            String[] coordenadasArray = coordenadasModel.getCoordenadas().split(", ");
            String latitud = coordenadasArray[0].replace(',', '.');
            String longitud = coordenadasArray[1].replace(',', '.');

            latitud = String.format(Locale.US, "%.7f", Double.parseDouble(latitud));
            longitud = String.format(Locale.US, "%.7f", Double.parseDouble(longitud));

            // Volver a armar la cadena de coordenadas
            String coordenadaFormateada = latitud + ", " + longitud;

            coordenadas.setCoordenadas(coordenadaFormateada);
            coordenadas.setRadio(coordenadasModel.getRadio());
            coordenadas.setSonidosVelocidadModel(coordenadasModel.getSonidosVelocidadModel());
            coordenadas.setSonidosGeocercaModel(coordenadasModel.getSonidosGeocercaModel());
            coordenadas.setSonidosGeocercaModel(coordenadasModel.getSonidosGeocercaModel());
            coordenadas.setRutasModel(coordenadasModel.getRutasModel());

            return coordenadasRepository.save(coordenadas);
        }
        return null;
    }

    public void Delete(Long id){
        coordenadasRepository.deleteById(id);
    }
}
