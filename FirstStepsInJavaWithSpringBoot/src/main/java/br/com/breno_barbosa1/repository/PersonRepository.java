package br.com.breno_barbosa1.repository;

import br.com.breno_barbosa1.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
