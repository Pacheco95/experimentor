package br.ufop.decom.adapter;

import br.ufop.decom.Task;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.List;

public class AdapterDependencyListToArrayList extends XmlAdapter<DependencyList, List<Task>> {

    @Override
    public List<Task> unmarshal(DependencyList v) {
        List<Task> tasks = new ArrayList<>();
        v.dependencies.forEach(dependencyEntry -> tasks.add(dependencyEntry.taskIdRef));
        return tasks;
    }

    @Override
    public DependencyList marshal(List<Task> v) {
        DependencyList list = new DependencyList();
        v.forEach(task -> list.dependencies.add(new DependencyEntry(task)));
        return list;
    }
}
