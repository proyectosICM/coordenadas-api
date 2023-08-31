package com.ICM.coordenadasRutaAPI.Services;

import com.ICM.coordenadasRutaAPI.Models.EmpresasModel;
import com.ICM.coordenadasRutaAPI.Models.PaisesModel;
import com.ICM.coordenadasRutaAPI.Repositories.EmpresasRepository;
import com.ICM.coordenadasRutaAPI.Repositories.PaisesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresasService {
    @Autowired
    EmpresasRepository empresasRepository;

    public EmpresasModel autenticar(EmpresasModel empresasModel) {
        String usuario = empresasModel.getUsuario();
        String password = empresasModel.getPassword();
        Optional<EmpresasModel> empresa = empresasRepository.findByUsuarioAndPassword(usuario, password);
        return empresa.orElse(null); // Devuelve la empresa si se encuentra, o null si no
    }


    //Crud

    public List<EmpresasModel> Get(){
        return empresasRepository.findAll();
    }

    public Optional<EmpresasModel> GetById(Long id){
        return empresasRepository.findById(id);
    }

    public EmpresasModel Save(EmpresasModel empresasModel) {
        return empresasRepository.save(empresasModel);
    }

    public EmpresasModel Edit(Long id, EmpresasModel empresasModel){
        Optional<EmpresasModel> existing = empresasRepository.findById(id);
        if(existing.isPresent()){
            EmpresasModel empresa = existing.get();
            empresa.setNombre(empresasModel.getNombre());
            return empresasRepository.save(empresa);
        }
        return null;
    }

    public void Delete(Long id){
        empresasRepository.deleteById(id);
    }
}
