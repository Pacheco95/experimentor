package br.ufop.decom;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Requirements {
    private int cores;
    private int ram;
    private int storage;
    private long timeout;

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
