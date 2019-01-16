/*
 * This file is not used by the application and should not be instantiated.
 * */

package br.ufop.decom.adapter;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class VarList {
    @XmlElement(name = "var", required = true)
    public List<VarEntry> vars;

    public VarList() {
        vars = new ArrayList<>();
    }
}
