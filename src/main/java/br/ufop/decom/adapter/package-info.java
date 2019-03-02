/**
 * This package contains Adapter classes required by jaxb to properly generate schema files from annotated classes.
 **/

@XmlSchema(
        // Desired target namespace for XML tags in experiment file
        namespace = "http://www.decom.ufop.br",
        elementFormDefault = XmlNsForm.QUALIFIED
)

package br.ufop.decom.adapter;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;