<%--
This file is part of SyMBA.
SyMBA is covered under the GNU Lesser General Public License (LGPL).
Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
To view the full licensing information for this software and ALL other files contained
in this distribution, please see LICENSE.txt
--%>
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<jsp:useBean id="counter" class="uk.ac.cisban.symba.webapp.util.CounterBean" scope="application">
</jsp:useBean>

<div id="Menu">
    <ul>
        <jsp:include page="menu-static.html"/>
        <%
            out.println( "<li>&nbsp;" );
            out.println( "Total Experiments: " + counter.getNumberOfExperiments() );
            out.println( "</li>" );

            out.println( "<li>&nbsp;" );
            out.println( "Total Data Files: " + counter.getNumberOfDataFiles() );
            out.println( "</li>" );
        %>
        <li><a href="logoff.jsp" title="Log Off">&nbsp;Log Off</a></li>
    </ul>
</div>
