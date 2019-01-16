/*
* This file is not used by the application and should not be instantiated.
* */

package br.ufop.decom.adapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlValue;

@SuppressWarnings("ALL")
public class VarEntry {
    @XmlAttribute(required = true)
    @XmlID
    public String varId;

    @XmlValue
    public String command;

    public VarEntry() {
        this("Unnamed", "");
    }

    public VarEntry(String varId, String command) {
        this.varId = varId;
        this.command = command;
    }
}
