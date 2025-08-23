package br.com.breno_barbosa1.file.exporter.contract;

import br.com.breno_barbosa1.data.dto.v1.PersonDTO;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PersonExporter {

    Resource ExportPeople(List<PersonDTO> people) throws Exception;
    Resource ExportPerson(PersonDTO person) throws Exception;
}
