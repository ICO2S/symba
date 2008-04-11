/*
 * ExperimentBean.java
 *
 * Created on 06 December 2006, 09:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package uk.ac.cisban.symba.webapp.util;

import fugeOM.Collection.FuGE;

import java.io.Serializable;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 *
 */
public class ExperimentBean implements Serializable {

    private FuGE fuGE;
    private String fugeEndurant;
    private String fugeIdentifier;
    private String experimentName;
    private String hypothesis;
    private String conclusion;


    /**
     * Creates a new instance of ExperimentBean
     */
    public ExperimentBean() {
    }

    public FuGE getFuGE() {
        return fuGE;
    }

    public void setFuGE(FuGE fuGE) {
        this.fuGE = fuGE;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis(String hypothesis) {
        this.hypothesis = hypothesis;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getFugeEndurant() {
        return fugeEndurant;
    }

    public void setFugeEndurant(String fugeEndurant) {
        this.fugeEndurant = fugeEndurant;
    }

    public String getFugeIdentifier() {
        return fugeIdentifier;
    }

    public void setFugeIdentifier( String fugeIdentifier ) {
        this.fugeIdentifier = fugeIdentifier;
    }

    public void clear() {
        this.fuGE = null;
        this.fugeEndurant = "";
        this.fugeIdentifier = "";
        this.experimentName = "";
        this.hypothesis = "";
    }
}
