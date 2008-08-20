<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<jsp:useBean id="softwareMeta" class="net.sourceforge.symba.webapp.util.SoftwareMetaInformationBean"
             scope="application"/>

<div id="Newcastle">
    <a href="http://www.ncl.ac.uk">
        <img alt="Newcastle University" src="pics/newcastle_50l.jpg"
             align="middle" border="0">
    </a>
</div>

<div id="Header"><img alt="logo" src="pics/lion.jpg" align="middle">&nbsp;&nbsp;&nbsp;
    <% out.println( softwareMeta.getName() ); %>
    Release <% out.println( softwareMeta.getVersion() ); %>
</div>
