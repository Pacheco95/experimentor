package br.ufop.decom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings({"unused"})
@XmlType(name = "requirementsType", propOrder = {"cores", "ram", "storage", "timeout"})
@XmlAccessorType(XmlAccessType.NONE)
public class Requirements {
    @XmlElement
    private int cores;

    @XmlElement
    private int ram;

    @XmlElement
    private int storage;

    @XmlElement
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

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
