package com.ICM.coordenadasRutaAPI.Services;

import com.ICM.coordenadasRutaAPI.Models.RutasModel;
import com.ICM.coordenadasRutaAPI.Models.TipoSModel;
import com.ICM.coordenadasRutaAPI.Repositories.TipoSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoSService {
    @Autowired
    private TipoSRepository tipoSRepository;

    public List<TipoSModel> getAllTypesSignals(){
        return tipoSRepository.findAll();
    }

    public Optional<TipoSModel> getTypeSignalsById(Long id){
        return tipoSRepository.findById(id);
    }
}
