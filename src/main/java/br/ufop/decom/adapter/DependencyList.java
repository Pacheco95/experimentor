package br.ufop.decom.adapter;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class DependencyList {
    @XmlElement(name = "dependency", required = true)
    public List<DependencyEntry> dependencies;
    public DependencyList() {
        dependencies = new ArrayList<>();
    }
}
