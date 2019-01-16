package br.ufop.decom.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class AdapterVarListToMap extends XmlAdapter<VarList, Map<String, String>> {

    @Override
    public Map<String, String> unmarshal(VarList varList) {
        Map<String, String> varsMap = new HashMap<>();
        varList.vars.forEach(varEntry -> varsMap.put(varEntry.varId, varEntry.command));
        return varsMap;
    }

    @Override
    public VarList marshal(Map<String, String> map) {
        VarList varList = new VarList();
        map.forEach((key, value) -> varList.vars.add(new VarEntry(key, value)));
        return varList;
    }
}
