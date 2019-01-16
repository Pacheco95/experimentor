package br.ufop.decom.adapter;

import br.ufop.decom.Task;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

@SuppressWarnings("ALL")
public class DependencyEntry {
    @XmlIDREF
    @XmlAttribute(required = true)
    public Task taskIdRef;

    public DependencyEntry() {
    }

    public DependencyEntry(Task taskIdRef) {
        this.taskIdRef = taskIdRef;
    }
}
