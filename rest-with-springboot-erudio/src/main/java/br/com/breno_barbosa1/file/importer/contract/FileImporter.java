package br.com.breno_barbosa1.file.importer.contract;

import br.com.breno_barbosa1.data.dto.v1.PersonDTO;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public interface FileImporter {

    List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}
