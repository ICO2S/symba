 # This file is part of SyMBA.
 # SyMBA is covered under the GNU Lesser General Public License (LGPL).
 # Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 # To view the full licensing information for this software and ALL other files contained
 # in this distribution, please see LICENSE.txt
 #
 # $LastChangedDate:$
 # $LastChangedRevision:$
 # $Author:$
 # $HeadURL:$
 #

create table users (
user_name CHARACTER VARYING(1024) not null,
password CHARACTER VARYING(1024) not null,
lsid CHARACTER VARYING(1024) not null,
primary key (lsid)
);
