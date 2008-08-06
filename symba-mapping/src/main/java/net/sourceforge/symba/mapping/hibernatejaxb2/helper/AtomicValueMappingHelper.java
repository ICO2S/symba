package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.measurement.AtomicValue;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonMeasurementAtomicValueType;

/**
 * Copyright Notice
 *
 * The MIT License
 *
 * Copyright (c) 2008 2007-8 Proteomics Standards Initiative / Microarray and Gene Expression Data Society
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Acknowledgements
 *  The authors wish to thank the Proteomics Standards Initiative for
 *  the provision of infrastructure and expertise in the form of the PSI
 *  Document Process that has been used to formalise this document.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: $
 */

public class AtomicValueMappingHelper implements MappingHelper<AtomicValue,FuGECommonMeasurementAtomicValueType> {

    public AtomicValue unmarshal( FuGECommonMeasurementAtomicValueType valueXML, AtomicValue value, Person performer ) {

        // set any AtomicValue-specific traits

        // default value
        if ( valueXML.getValue() != null ) {
            value.setValue( valueXML.getValue() );
        }

        return value;
    }

    public FuGECommonMeasurementAtomicValueType marshal( FuGECommonMeasurementAtomicValueType valueXML,
                                                                   AtomicValue value ) {

        // set any AtomicValue-specific traits
        if ( value.getValue() != null )
            valueXML.setValue( value.getValue() );

        return valueXML;
    }
}
