package br.ufop.decom;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings({"unused"})
@XmlType(name = "requirementsType", propOrder = {"cores", "ram", "storage", "timeout"})
@XmlAccessorType(XmlAccessType.NONE)
public class Requirements {
    @XmlElement
    @Getter @Setter
    private int cores;

    @XmlElement
    @Getter @Setter
    private int ram;

    @XmlElement
    @Getter @Setter
    private int storage;

    @XmlElement
    @Getter @Setter
    private long timeout;

    public Requirements() {
        this(0, 0, 0, 0);
    }

    public Requirements(int cores, int ram, int storage, int timeout) {
        this.cores = cores;
        this.ram = ram;
        this.storage = storage;
        this.timeout = timeout;
    }
}
