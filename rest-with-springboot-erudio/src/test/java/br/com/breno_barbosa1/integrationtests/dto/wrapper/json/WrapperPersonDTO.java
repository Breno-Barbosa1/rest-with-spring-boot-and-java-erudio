package br.com.breno_barbosa1.integrationtests.dto.wrapper.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class WrapperPersonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("_embedded")
    private PersonEmbeddedDTO embedded;

    public WrapperPersonDTO() {}

    public PersonEmbeddedDTO getEmbedded() {
        return embedded;
    }

    public void setEmbedded(PersonEmbeddedDTO embedded) {
        this.embedded = embedded;
    }
}
