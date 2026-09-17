package persistence.model;

import java.io.Serializable;

public record PortEntity(String portId, String name, PortType portType, Integer gpio,
                         boolean enabled) implements Serializable {

    public enum PortType {
        INPUT, OUTPUT
    }

}
