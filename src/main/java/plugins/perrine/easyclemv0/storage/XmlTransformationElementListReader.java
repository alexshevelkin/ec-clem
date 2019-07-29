/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.perrine.easyclemv0.storage;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static plugins.perrine.easyclemv0.storage.XmlTransformation.transformationDateAttributeName;
import static plugins.perrine.easyclemv0.storage.XmlTransformation.transformationElementName;

public class XmlTransformationElementListReader {

    private XmlTransformationReader xmlTransformationReader = new XmlTransformationReader();

    public Element getLastTransformationElement(Document document) {
        List<Element> list = read(document);
        sortTransformationElementsByDate(list);
        return list.get(list.size() - 1);
    }

    public List<TransformationSchema> getTransformationList(Document document) {
        List<TransformationSchema> transformationSchemaList = new ArrayList<>();
        List<Element> elementList = read(document);
        sortTransformationElementsByDate(elementList);
        for(Element element : elementList) {
            transformationSchemaList.add(xmlTransformationReader.read(element));
        }
        return transformationSchemaList;
    }

    private List<Element> read(Document document) {
        return XMLUtil.getElements(document.getDocumentElement(), transformationElementName);
    }

    private void sortTransformationElementsByDate(List<Element> list) {
        list.sort(Comparator.comparing(o -> ZonedDateTime.parse(o.getAttribute(transformationDateAttributeName))));
    }
}