
    alter table ACTION 
        drop constraint ACTION_PROTOCOL_FKC;

    alter table ACTION 
        drop constraint ACTION_PROTOCOL_REFERENCE_FKC;

    alter table ACTION 
        drop constraint ACTIONIFKC;

    alter table ACTION_APPLICATION 
        drop constraint ACTION_APPLICATION_PROT_APP_RC;

    alter table ACTION_APPLICATION 
        drop constraint ACTION_APPLICATION_ACTION_FKC;

    alter table ACTION_APPLICATION 
        drop constraint ACTION_APPLICATION_ACTION_DEVC;

    alter table ACTION_APPLICATION 
        drop constraint ACTION_APPLICATION_PROTOCOL_AC;

    alter table ACTION_APPLICATION 
        drop constraint ACTION_APPLICATIONIFKC;

    alter table AFFILIATIONS 
        drop constraint PERSON_AFFILIATIONS_FKC;

    alter table AFFILIATIONS 
        drop constraint ORGANIZATION_PEOPLE_FKC;

    alter table ALL_BIBLIOGRAPHIC_REFERENCES 
        drop constraint REFERENCEABLE_COLLECTION_ALL_C;

    alter table ALL_BIBLIOGRAPHIC_REFERENCES 
        drop constraint BIBLIOGRAPHIC_REFERENCE_REFERC;

    alter table ALL_CONTACTS 
        drop constraint CONTACT_AUDIT_COLLECTIONS_FKC;

    alter table ALL_CONTACTS 
        drop constraint AUDIT_COLLECTION_ALL_CONTACTSC;

    alter table ALL_DATA 
        drop constraint DATA_DATA_COLLECTIONS_FKC;

    alter table ALL_DATA 
        drop constraint DATA_COLLECTION_ALL_DATA_FKC;

    alter table ALL_DATA_PARTITIONS 
        drop constraint DATA_COLLECTION_ALL_DATA_PARTC;

    alter table ALL_DATA_PARTITIONS 
        drop constraint DATA_PARTITION_DATA_COLLECTIOC;

    alter table ALL_EQUIPMENT 
        drop constraint PROTOCOL_COLLECTION_ALL_EQUIPC;

    alter table ALL_EQUIPMENT 
        drop constraint EQUIPMENT_PROTOCOL_COLLECTIONC;

    alter table ALL_PROTOCOL_APPLICATIONS 
        drop constraint INVESTIGATION_COMPONENT_ALL_PC;

    alter table ALL_PROTOCOL_APPLICATIONS 
        drop constraint PROTOCOL_APPLICATION_INVESTIGC;

    alter table ALL_PROTOCOL_APPS 
        drop constraint PROTOCOL_COLLECTION_ALL_PROTOC;

    alter table ALL_PROTOCOL_APPS 
        drop constraint PROTOCOL_APPLICATION_PROTOCOLC;

    alter table ALL_SEQUENCE_ANNOTATIONS 
        drop constraint SEQUENCE_ANNOTATION_CONC_MOLEC;

    alter table ALL_SEQUENCE_ANNOTATIONS 
        drop constraint CONC_MOLECULE_COLLECTION_ALL_C;

    alter table ALL_SOFTWARE 
        drop constraint PROTOCOL_COLLECTION_ALL_SOFTWC;

    alter table ALL_SOFTWARE 
        drop constraint SOFTWARE_PROTOCOL_COLLECTIONSC;

    alter table ANNOTATIONS 
        drop constraint ONTOLOGY_TERM_DESCRIBABLES_FKC;

    alter table ANNOTATIONS 
        drop constraint DESCRIBABLE_ANNOTATIONS_FKC;

    alter table ATOMIC_PARAMETER_VALUE 
        drop constraint ATOMIC_PARAMETER_VALUEIFKC;

    alter table ATOMIC_VALUE 
        drop constraint ATOMIC_VALUEIFKC;

    alter table AUDIT 
        drop constraint AUDIT_PERFORMER_FKC;

    alter table AUDIT 
        drop constraint AUDIT_DESCRIBABLE_FKC;

    alter table AUDIT 
        drop constraint AUDITIFKC;

    alter table AUDIT_COLLECTION 
        drop constraint AUDIT_COLLECTIONIFKC;

    alter table BIBLIOGRAPHIC_REFERENCE 
        drop constraint BIBLIOGRAPHIC_REFERENCEIFKC;

    alter table BIBLIOGRAPHIC_REFERENCES 
        drop constraint BIBLIOGRAPHIC_REFERENCE_IDENTC;

    alter table BIBLIOGRAPHIC_REFERENCES 
        drop constraint IDENTIFIABLE_BIBLIOGRAPHIC_REC;

    alter table BIB_REF_ENDURANT 
        drop constraint BIB_REF_ENDURANTIFKC;

    alter table BOOLEAN_PARAMETER_VALUE 
        drop constraint BOOLEAN_PARAMETER_VALUEIFKC;

    alter table BOOLEAN_VALUE 
        drop constraint BOOLEAN_VALUEIFKC;

    alter table CHARACTERISTICS 
        drop constraint MAT_CHAR_FKC;

    alter table CHARACTERISTICS 
        drop constraint MATERIAL_CHARACTERISTICS_FKC;

    alter table COMPLEX_PARAMETER_VALUE 
        drop constraint COMPLEX_PARAMETER_VALUE_PARAMC;

    alter table COMPLEX_PARAMETER_VALUE 
        drop constraint COMPLEX_PARAMETER_VALUEIFKC;

    alter table COMPLEX_VALUE 
        drop constraint COMPLEX_VALUE__DEFAULT_VALUE_C;

    alter table COMPLEX_VALUE 
        drop constraint COMPLEX_VALUEIFKC;

    alter table COMPONENTS 
        drop constraint MATERIAL_MATERIALS_FKC;

    alter table COMPONENTS 
        drop constraint GENERIC_MATERIAL_GENERIC_MATEC;

    alter table COMPONENTS 
        drop constraint GENERIC_MATERIAL_COMPONENTS_FC;

    alter table COMPONENTS 
        drop constraint MATERIAL_COMPONENTS_FKC;

    alter table COMPONENT_DESIGN_TYPES 
        drop constraint ONTOLOGY_TERM_INVESTIGATION_CC;

    alter table COMPONENT_DESIGN_TYPES 
        drop constraint INVESTIGATION_COMPONENT_COMPOC;

    alter table CONCEPTUAL_MOLECULE 
        drop constraint CONCEPTUAL_MOLECULEIFKC;

    alter table CONCEP_MOLS 
        drop constraint CONCEPTUAL_MOLECULE_CONC_MOLEC;

    alter table CONCEP_MOLS 
        drop constraint CONC_MOLECULE_COLLECTION_CONCC;

    alter table CONC_MOLECULE_COLLECTION 
        drop constraint CONC_MOLECULE_COLLECTIONIFKC;

    alter table CONTACT 
        drop constraint CONTACTIFKC;

    alter table CONTACT_ROLE 
        drop constraint CONTACT_ROLE_PARAMETERIZABLE_C;

    alter table CONTACT_ROLE 
        drop constraint CONTACT_ROLE_ROLE_FKC;

    alter table CONTACT_ROLE 
        drop constraint CONTACT_ROLE_MATERIAL_FKC;

    alter table CONTACT_ROLE 
        drop constraint CONTACT_ROLE_INVESTIGATION_FKC;

    alter table CONTACT_ROLE 
        drop constraint CONTACT_ROLE_PROTOCOL_APPLICAC;

    alter table CONTACT_ROLE 
        drop constraint CONTACT_ROLE_DATABASE_FKC;

    alter table CONTACT_ROLE 
        drop constraint CONTACT_ROLE_CONTACT_FKC;

    alter table CONTACT_ROLE 
        drop constraint CONTACT_ROLEIFKC;

    alter table DATA 
        drop constraint GPA_OUTDATA_FKC;

    alter table DATA 
        drop constraint DATA_PROTOCOL_APPLICATION_FKC;

    alter table DATA 
        drop constraint DATAIFKC;

    alter table DATABASE 
        drop constraint DATABASEIFKC;

    alter table DATABASE_ENDURANT 
        drop constraint DATABASE_ENDURANTIFKC;

    alter table DATABASE_ENTRY 
        drop constraint DATABASE_ENTRY_IDENTIFIABLE_FC;

    alter table DATABASE_ENTRY 
        drop constraint DATABASE_ENTRY_DATABASE_FKC;

    alter table DATABASE_ENTRY 
        drop constraint DATABASE_ENTRYIFKC;

    alter table DATA_COLLECTION 
        drop constraint DATA_COLLECTIONIFKC;

    alter table DATA_DIMENSION 
        drop constraint DIMENSION_DATAS_FKC;

    alter table DATA_DIMENSION 
        drop constraint DATA_DATA_DIMENSION_FKC;

    alter table DATA_PARTITION 
        drop constraint DATA_PARTITION_PARTITIONED_DAC;

    alter table DATA_PARTITION 
        drop constraint DATA_PARTITIONIFKC;

    alter table DATA_PARTITIONS 
        drop constraint FACTOR_VALUE_DATA_PARTITIONS_C;

    alter table DATA_PARTITIONS 
        drop constraint DATA_PARTITION_FACTOR_VALUES_C;

    alter table DATA_PART_ENDURANT 
        drop constraint DATA_PART_ENDURANTIFKC;

    alter table DATA_PROPERTY 
        drop constraint DATA_PROPERTYIFKC;

    alter table DATA_PROPERTY_ENDURANT 
        drop constraint DATA_PROPERTY_ENDURANTIFKC;

    alter table DEFAULT_VALUE 
        drop constraint DEFAULT_VALUEIFKC;

    alter table DESCRIBABLE 
        drop constraint DESCRIBABLE_EXTURI_FKC;

    alter table DESCRIBABLE 
        drop constraint DESCRIBABLE_SECURITY_FKC;

    alter table DESCRIPTION 
        drop constraint DESCRIPTION_DESCRIBABLE_FKC;

    alter table DESCRIPTION 
        drop constraint DESCRIPTIONIFKC;

    alter table DIMENSION 
        drop constraint DIMENSION_DATA_COLLECTION_FKC;

    alter table DIMENSION 
        drop constraint DIMENSION_DIMENSION_TYPE_FKC;

    alter table DIMENSION 
        drop constraint DIMENSIONIFKC;

    alter table DIMENSION_ELEMENT 
        drop constraint DIMENSION_ELEMENT_DIMENSION_FC;

    alter table DIMENSION_ELEMENT 
        drop constraint DIMENSION_ELEMENTIFKC;

    alter table DIMENSION_ELEMENT_SET 
        drop constraint DIMENSION_ELEMENT_DATA_PARTITC;

    alter table DIMENSION_ELEMENT_SET 
        drop constraint DATA_PARTITION_DIMENSION_ELEMC;

    alter table DIMENSION_ENDURANT 
        drop constraint DIMENSION_ENDURANTIFKC;

    alter table DIM_ELEM_ENDURANT 
        drop constraint DIM_ELEM_ENDURANTIFKC;

    alter table ENDURANT 
        drop constraint ENDURANTIFKC;

    alter table EQUIPMENT 
        drop constraint EQUIPMENT_MODEL_FKC;

    alter table EQUIPMENT 
        drop constraint EQUIPMENT_MAKE_FKC;

    alter table EQUIPMENT 
        drop constraint EQUIPMENTIFKC;

    alter table EQUIPMENT_APPLICATION 
        drop constraint EQUIPMENT_APPLICATION_APPLIEDC;

    alter table EQUIPMENT_APPLICATION 
        drop constraint EQUIPMENT_APPLICATION_PROTOCOC;

    alter table EQUIPMENT_APPLICATION 
        drop constraint EQUIPMENT_APPLICATIONIFKC;

    alter table EQUIPMENT_PARTS 
        drop constraint EQUIPMENT_EQUIPMENTS_FKC;

    alter table EQUIPMENT_PARTS 
        drop constraint EQUIPMENT_EQUIPMENT_PARTS_FKC;

    alter table EQUIP_APP_ENDURANT 
        drop constraint EQUIP_APP_ENDURANTIFKC;

    alter table EXTERNAL_DATA 
        drop constraint EXTERNAL_DATA_EXTERNAL_FORMATC;

    alter table EXTERNAL_DATA 
        drop constraint EXTERNAL_DATA_FILE_FORMAT_FKC;

    alter table EXTERNAL_DATA 
        drop constraint EXTERNAL_DATAIFKC;

    alter table EXTERNAL_DATA_ENDURANT 
        drop constraint EXTERNAL_DATA_ENDURANTIFKC;

    alter table FACTOR 
        drop constraint FACTOR_FACTOR_CATEGORY_FKC;

    alter table FACTOR 
        drop constraint FACTORIFKC;

    alter table FACTORS 
        drop constraint INVESTIGATION_COMPONENT_FACTOC;

    alter table FACTORS 
        drop constraint FACTOR_INVESTIGATION_COMPONENC;

    alter table FACTOR_COLLECTION 
        drop constraint INVESTIGATION_COLLECTION_FACTC;

    alter table FACTOR_COLLECTION 
        drop constraint FACTOR_INVESTIGATION_COLLECTIC;

    alter table FACTOR_ENDURANT 
        drop constraint FACTOR_ENDURANTIFKC;

    alter table FACTOR_VALUE 
        drop constraint FACTOR_VALUE_VALUE_FKC;

    alter table FACTOR_VALUE 
        drop constraint FACTOR_VALUE_FACTOR_FKC;

    alter table FACTOR_VALUE 
        drop constraint FACTOR_VALUEIFKC;

    alter table FU_G_E 
        drop constraint FU_G_E_CONCEP_MOL_COLLECTION_C;

    alter table FU_G_E 
        drop constraint FU_G_E_PROVIDER_FKC;

    alter table FU_G_E 
        drop constraint FU_G_E_DATA_COLLECTION_FKC;

    alter table FU_G_E 
        drop constraint FU_G_E_MATERIAL_COLLECTION_FKC;

    alter table FU_G_E 
        drop constraint FU_G_E_INVESTIGATION_COLLECTIC;

    alter table FU_G_E 
        drop constraint FU_G_E_ONTOLOGY_COLLECTION_FKC;

    alter table FU_G_E 
        drop constraint FU_G_E_PROTOCOL_COLLECTION_FKC;

    alter table FU_G_E 
        drop constraint FU_G_E_AUDIT_COLLECTION_FKC;

    alter table FU_G_E 
        drop constraint FU_G_E_REFERENCEABLE_COLLECTIC;

    alter table FU_G_E 
        drop constraint FU_G_EIFKC;

    alter table FU_G_E_ENDURANT 
        drop constraint FU_G_E_ENDURANTIFKC;

    alter table GENERIC_ACTION 
        drop constraint GENERIC_ACTION_ACTION_TERM_FKC;

    alter table GENERIC_ACTION 
        drop constraint GENERIC_ACTION_GEN_PROTOCOL_RC;

    alter table GENERIC_ACTION 
        drop constraint GENERIC_ACTION_GENERIC_PROTOCC;

    alter table GENERIC_ACTION 
        drop constraint GENERIC_ACTIONIFKC;

    alter table GENERIC_ACTION_ENDURANT 
        drop constraint GENERIC_ACTION_ENDURANTIFKC;

    alter table GENERIC_EQUIPMENT 
        drop constraint GENERIC_EQUIPMENTIFKC;

    alter table GENERIC_INPUT_DATA 
        drop constraint GPA_INDATA_FKC;

    alter table GENERIC_INPUT_DATA 
        drop constraint DATA_INPUT_GPA_FKC;

    alter table GENERIC_MATERIAL 
        drop constraint GENERIC_MATERIALIFKC;

    alter table GENERIC_MATERIAL_ENDURANT 
        drop constraint GENERIC_MATERIAL_ENDURANTIFKC;

    alter table GENERIC_MAT_MEAS 
        drop constraint GENERIC_MAT_MEAS_GENERIC_PROTC;

    alter table GENERIC_MAT_MEAS 
        drop constraint GENERIC_MAT_MEAS_GENERIC_MEASC;

    alter table GENERIC_MAT_MEAS 
        drop constraint GENERIC_MAT_MEASIFKC;

    alter table GENERIC_PARAMETER 
        drop constraint GENERIC_PARAMETER_GENERIC_SOFC;

    alter table GENERIC_PARAMETER 
        drop constraint GENERIC_PARAMETER_PARAMETER_TC;

    alter table GENERIC_PARAMETER 
        drop constraint GENERIC_PARAMETER_GENERIC_ACTC;

    alter table GENERIC_PARAMETER 
        drop constraint GENERIC_PARAMETER_GENERIC_EQUC;

    alter table GENERIC_PARAMETER 
        drop constraint GENERIC_PARAMETER_GENERIC_PROC;

    alter table GENERIC_PARAMETER 
        drop constraint GENERIC_PARAMETERIFKC;

    alter table GENERIC_PROTOCOL 
        drop constraint GENERIC_PROTOCOLIFKC;

    alter table GENERIC_PROTOCOL_APPLICATION 
        drop constraint GENERIC_PROTOCOL_APPLICATION_C;

    alter table GENERIC_PROTOCOL_APPLICATION 
        drop constraint GENERIC_PROTOCOL_APPLICATIONIFKC;

    alter table GENERIC_SOFTWARE 
        drop constraint GENERIC_SOFTWAREIFKC;

    alter table GEN_EQUIP_ENDURANT 
        drop constraint GEN_EQUIP_ENDURANTIFKC;

    alter table GEN_EQUIP_PARTS 
        drop constraint GENERIC_EQUIPMENT_GEN_EQUIP_PC;

    alter table GEN_EQUIP_PARTS 
        drop constraint GENERIC_EQUIPMENT_GENERIC_EQUC;

    alter table GEN_EQ_TO_SOFT 
        drop constraint GENERIC_SOFTWARE_GEN_EQUIPMENC;

    alter table GEN_EQ_TO_SOFT 
        drop constraint GENERIC_EQUIPMENT_SOFTWARE_FKC;

    alter table GEN_PARAM_ENDURANT 
        drop constraint GEN_PARAM_ENDURANTIFKC;

    alter table GEN_PROTOCOL_ENDURANT 
        drop constraint GEN_PROTOCOL_ENDURANTIFKC;

    alter table GEN_PRTCL_APP_ENDURANT 
        drop constraint GEN_PRTCL_APP_ENDURANTIFKC;

    alter table GEN_PRTCL_TO_EQUIP 
        drop constraint GENERIC_PROTOCOL_GEN_PRTCL_TOC;

    alter table GEN_PRTCL_TO_EQUIP 
        drop constraint GENERIC_EQUIPMENT_GENERIC_PROC;

    alter table GEN_SOFTWARE 
        drop constraint GENERIC_PROTOCOL_GEN_SOFTWAREC;

    alter table GEN_SOFTWARE 
        drop constraint GENERIC_SOFTWARE_GENERIC_PROTC;

    alter table GEN_SOFTWARE_ENDURANT 
        drop constraint GEN_SOFTWARE_ENDURANTIFKC;

    alter table HIGHER_LEVEL_ANALYSES 
        drop constraint HIGHER_LEVEL_ANALYSIS_DATA_COC;

    alter table HIGHER_LEVEL_ANALYSES 
        drop constraint DATA_COLLECTION_HIGHER_LEVEL_C;

    alter table HIGHER_LEVEL_ANALYSIS 
        drop constraint HIGHER_LEVEL_ANALYSIS_CONCLUSC;

    alter table HIGHER_LEVEL_ANALYSIS 
        drop constraint HIGHER_LEVEL_ANALYSIS_HYPOTHEC;

    alter table HIGHER_LEVEL_ANALYSIS 
        drop constraint HIGHER_LEVEL_ANALYSISIFKC;

    alter table IDENTIFIABLE 
        drop constraint IDENTIFIABLE_ENDURANT_FKC;

    alter table IDENTIFIABLE 
        drop constraint IDENTIFIABLEIFKC;

    alter table INPUT_DATA 
        drop constraint PROTOCOL_APPLICATION_INPUT_DAC;

    alter table INPUT_DATA 
        drop constraint DATA_PROTOCOL_APPLICATIONS_FKC;

    alter table INPUT_PARTITIONS 
        drop constraint PARTITION_IN_FKC;

    alter table INPUT_PARTITIONS 
        drop constraint PARTITION_PAIR_INPUT_PARTITIOC;

    alter table INTERNAL_DATA 
        drop constraint INTERNAL_DATAIFKC;

    alter table INTERNAL_DATA_ENDURANT 
        drop constraint INTERNAL_DATA_ENDURANTIFKC;

    alter table INVESTIGATION 
        drop constraint INVESTIGATIONIFKC;

    alter table INVESTIGATIONS 
        drop constraint INVESTIGATION_COLLECTION_INVEC;

    alter table INVESTIGATIONS 
        drop constraint INVESTIGATION_INVESTIGATION_CC;

    alter table INVESTIGATION_COLLECTION 
        drop constraint INVESTIGATION_COLLECTIONIFKC;

    alter table INVESTIGATION_COMPONENT 
        drop constraint INVESTIGATION_COMPONENT_REPLIC;

    alter table INVESTIGATION_COMPONENT 
        drop constraint INVESTIGATION_COMPONENT_NORMAC;

    alter table INVESTIGATION_COMPONENT 
        drop constraint INVESTIGATION_COMPONENT_INVESC;

    alter table INVESTIGATION_COMPONENT 
        drop constraint INVESTIGATION_COMPONENT_QUALIC;

    alter table INVESTIGATION_COMPONENT 
        drop constraint INVESTIGATION_COMPONENTIFKC;

    alter table INVESTIGATION_ENDURANT 
        drop constraint INVESTIGATION_ENDURANTIFKC;

    alter table INVESTIGATION_TYPES 
        drop constraint ONTOLOGY_TERM_INVESTIGATIONS_C;

    alter table INVESTIGATION_TYPES 
        drop constraint INVESTIGATION_INVESTIGATION_TC;

    alter table INV_COMPONENT_ENDURANT 
        drop constraint INV_COMPONENT_ENDURANTIFKC;

    alter table MATERIAL 
        drop constraint MATERIAL_GENERIC_PROTOCOL_APPC;

    alter table MATERIAL 
        drop constraint MATERIAL_MATERIAL_TYPE_FKC;

    alter table MATERIAL 
        drop constraint MATERIAL_PROTOCOL_APPLICATIONC;

    alter table MATERIAL 
        drop constraint MATERIALIFKC;

    alter table MATERIALS 
        drop constraint MATERIAL_COLLECTION_MATERIALSC;

    alter table MATERIALS 
        drop constraint MATERIAL_MATERIAL_COLLECTIONSC;

    alter table MATERIAL_COLLECTION 
        drop constraint MATERIAL_COLLECTIONIFKC;

    alter table MATERIAL_MEASUREMENT 
        drop constraint MATERIAL_MEASUREMENT_MEASUREDC;

    alter table MATERIAL_MEASUREMENT 
        drop constraint MATERIAL_MEASUREMENT_MEASUREMC;

    alter table MATERIAL_MEASUREMENT 
        drop constraint MATERIAL_MEASUREMENT_PROTOCOLC;

    alter table MATERIAL_MEASUREMENT 
        drop constraint MATERIAL_MEASUREMENTIFKC;

    alter table MEMBERS 
        drop constraint SECURITY_GROUP_MEMBERS_FKC;

    alter table MEMBERS 
        drop constraint CONTACT_SECURITY_GROUPS_FKC;

    alter table NAME_VALUE_TYPE 
        drop constraint NAME_VALUE_TYPE_DESCRIBABLE_FC;

    alter table NAME_VALUE_TYPE 
        drop constraint NAME_VALUE_TYPEIFKC;

    alter table OBJECT_PROPERTY 
        drop constraint OBJECT_PROPERTYIFKC;

    alter table OBJ_PROPERTY_ENDURANT 
        drop constraint OBJ_PROPERTY_ENDURANTIFKC;

    alter table ONTOLOGY_COLLECTION 
        drop constraint ONTOLOGY_COLLECTIONIFKC;

    alter table ONTOLOGY_INDIVIDUAL 
        drop constraint ONTOLOGY_INDIVIDUAL_OBJECT_PRC;

    alter table ONTOLOGY_INDIVIDUAL 
        drop constraint ONTOLOGY_INDIVIDUALIFKC;

    alter table ONTOLOGY_PROPERTY 
        drop constraint ONTOLOGY_PROPERTY_ONTOLOGY_INC;

    alter table ONTOLOGY_PROPERTY 
        drop constraint ONTOLOGY_PROPERTYIFKC;

    alter table ONTOLOGY_SOURCE 
        drop constraint ONTOLOGY_SOURCEIFKC;

    alter table ONTOLOGY_SOURCES 
        drop constraint ONTOLOGY_SOURCE_ONTOLOGY_COLLC;

    alter table ONTOLOGY_SOURCES 
        drop constraint ONTOLOGY_COLLECTION_ONTOLOGY_C;

    alter table ONTOLOGY_TERM 
        drop constraint ONTOLOGY_TERM_ONTOLOGY_SOURCEC;

    alter table ONTOLOGY_TERM 
        drop constraint ONTOLOGY_TERMIFKC;

    alter table ONTOLOGY_TERMS 
        drop constraint ONTOLOGY_TERM_FKC;

    alter table ONTOLOGY_TERMS 
        drop constraint ONTOLOGY_TERM_ONTOLOGY_COLLECC;

    alter table ONTO_INDV_ENDURANT 
        drop constraint ONTO_INDV_ENDURANTIFKC;

    alter table ONTO_SOURCE_ENDURANT 
        drop constraint ONTO_SOURCE_ENDURANTIFKC;

    alter table ORGANIZATION 
        drop constraint ORGANIZATION_PARENT_FKC;

    alter table ORGANIZATION 
        drop constraint ORGANIZATIONIFKC;

    alter table ORGANIZATION_ENDURANT 
        drop constraint ORGANIZATION_ENDURANTIFKC;

    alter table OUTPUT_PARTITIONS 
        drop constraint PARTITION_PAIR_OUTPUT_PARTITIC;

    alter table OUTPUT_PARTITIONS 
        drop constraint PARTITION_OUT_FKC;

    alter table OWNER 
        drop constraint CONTACT_SECURITIES_FKC;

    alter table OWNER 
        drop constraint SECURITY_OWNER_FKC;

    alter table PARAMETER 
        drop constraint PARAMETER_UNIT_FKC;

    alter table PARAMETER 
        drop constraint PARAMETER_DATA_TYPE_FKC;

    alter table PARAMETER 
        drop constraint PARAMETER_ACTION_FKC;

    alter table PARAMETER 
        drop constraint PARAMETER_PARAMETERIZABLE_FKC;

    alter table PARAMETER 
        drop constraint PARAMETER_DEFAULT_VALUE_FKC;

    alter table PARAMETER 
        drop constraint PARAMETERIFKC;

    alter table PARAMETERIZABLE 
        drop constraint PARAMETERIZABLEIFKC;

    alter table PARAMETERIZABLE_APPLICATION 
        drop constraint PARAMETERIZABLE_APPLICATIONIFKC;

    alter table PARAMETERIZABLE_TYPES 
        drop constraint PARAMETERIZABLE_PARAMETERIZABC;

    alter table PARAMETERIZABLE_TYPES 
        drop constraint ONTOLOGY_TERM_PARAMETERIZABLEC;

    alter table PARAMETER_VALUE 
        drop constraint PARAMETER_VALUE_PARAMETER_FKC;

    alter table PARAMETER_VALUE 
        drop constraint PARAMETER_VALUE_PARAMETERIZABC;

    alter table PARAMETER_VALUE 
        drop constraint PARAMETER_VALUEIFKC;

    alter table PARTITION_PAIR 
        drop constraint PARTITION_PAIR_PARTITION_PAIRC;

    alter table PARTITION_PAIR 
        drop constraint PARTITION_PAIR_PROTOCOL_APPLIC;

    alter table PARTITION_PAIR 
        drop constraint PARTITION_PAIRIFKC;

    alter table PERSON 
        drop constraint PERSONIFKC;

    alter table PERSON_ENDURANT 
        drop constraint PERSON_ENDURANTIFKC;

    alter table PROTOCOL 
        drop constraint PROTOCOLIFKC;

    alter table PROTOCOLS 
        drop constraint PROTOCOL_COLLECTION_PROTOCOLSC;

    alter table PROTOCOLS 
        drop constraint PROTOCOL_PROTOCOL_COLLECTIONSC;

    alter table PROTOCOL_APPLICATION 
        drop constraint PROTOCOL_APPLICATION_DEVIATIOC;

    alter table PROTOCOL_APPLICATION 
        drop constraint PROTOCOL_APPLICATIONIFKC;

    alter table PROTOCOL_COLLECTION 
        drop constraint PROTOCOL_COLLECTIONIFKC;

    alter table PROTOCOL_EQUIPMENT 
        drop constraint EQUIPMENT_PROTOCOLS_FKC;

    alter table PROTOCOL_EQUIPMENT 
        drop constraint PROTOCOL_PROTOCOL_EQUIPMENT_FC;

    alter table PROTOCOL_INPUT_TYPES 
        drop constraint P_INPUTTYPES_FK;

    alter table PROTOCOL_INPUT_TYPES 
        drop constraint PROTOCOL_INPUT_TYPES_FKC;

    alter table PROTOCOL_OUTPUT_TYPES 
        drop constraint P_OUTPUTTYPES_FKC;

    alter table PROTOCOL_OUTPUT_TYPES 
        drop constraint PROTOCOL_OUTPUT_TYPES_FKC;

    alter table PROVIDER 
        drop constraint PROVIDER_PROVIDER_FKC;

    alter table PROVIDER 
        drop constraint PROVIDER_PRODUCING_SOFTWARE_FC;

    alter table PROVIDER 
        drop constraint PROVIDERIFKC;

    alter table PROVIDER_ENDURANT 
        drop constraint PROVIDER_ENDURANTIFKC;

    alter table QUALITY_CONTROL_STATISTICS 
        drop constraint Material_QC_FKC;

    alter table QUALITY_CONTROL_STATISTICS 
        drop constraint MATERIAL_QUALITY_CONTROL_STATC;

    alter table RANGE_PARAMETER_VALUE 
        drop constraint RANGE_PARAMETER_VALUEIFKC;

    alter table RANGE_VALUE 
        drop constraint RANGE_VALUEIFKC;

    alter table RC_TO_DATABASE 
        drop constraint REFERENCEABLE_COLLECTION_RC_TC;

    alter table RC_TO_DATABASE 
        drop constraint DATABASE_REFERENCEABLE_COLLECC;

    alter table REFERENCEABLE_COLLECTION 
        drop constraint REFERENCEABLE_COLLECTIONIFKC;

    alter table SECURITY 
        drop constraint SECURITYIFKC;

    alter table SECURITY_ACCESS 
        drop constraint SECURITY_ACCESS_ACCESS_RIGHT_C;

    alter table SECURITY_ACCESS 
        drop constraint SECURITY_ACCESS_ACCESS_GROUP_C;

    alter table SECURITY_ACCESS 
        drop constraint SECURITY_ACCESS_SECURITY_FKC;

    alter table SECURITY_ACCESS 
        drop constraint SECURITY_ACCESSIFKC;

    alter table SECURITY_COLLECTION 
        drop constraint SECURITY_AUDIT_COLLECTIONS_FKC;

    alter table SECURITY_COLLECTION 
        drop constraint AUDIT_COLLECTION_SECURITY_COLC;

    alter table SECURITY_ENDURANT 
        drop constraint SECURITY_ENDURANTIFKC;

    alter table SECURITY_GROUP 
        drop constraint SECURITY_GROUPIFKC;

    alter table SECURITY_GROUPS 
        drop constraint AUDIT_COLLECTION_SECURITY_GROC;

    alter table SECURITY_GROUPS 
        drop constraint SECURITY_GROUP_AUDIT_COLLECTIC;

    alter table SEC_GROUP_ENDURANT 
        drop constraint SEC_GROUP_ENDURANTIFKC;

    alter table SEQUENCE 
        drop constraint SEQUENCE_SEQUENCE_ANNOTATIONSC;

    alter table SEQUENCE 
        drop constraint SEQUENCEIFKC;

    alter table SEQUENCE_ANNOTATION 
        drop constraint SEQUENCE_ANNOTATION_POLYMER_TC;

    alter table SEQUENCE_ANNOTATION 
        drop constraint SEQUENCE_ANNOTATION_TYPE_FKC;

    alter table SEQUENCE_ANNOTATION 
        drop constraint SEQUENCE_ANNOTATION_SPECIES_FC;

    alter table SEQUENCE_ANNOTATION 
        drop constraint SEQUENCE_ANNOTATIONIFKC;

    alter table SEQUENCE_ENDURANT 
        drop constraint SEQUENCE_ENDURANTIFKC;

    alter table SEQ_ANNOT_ENDURANT 
        drop constraint SEQ_ANNOT_ENDURANTIFKC;

    alter table SOFTWARE 
        drop constraint SOFTWAREIFKC;

    alter table SOFTWARE2_EQUIPMENT 
        drop constraint EQUIPMENT_SOFTWARES_FKC;

    alter table SOFTWARE2_EQUIPMENT 
        drop constraint SOFTWARE_EQUIPMENT_FKC;

    alter table SOFTWARE_APPLICATION 
        drop constraint SOFTWARE_APPLICATION_APPLIED_C;

    alter table SOFTWARE_APPLICATION 
        drop constraint SOFTWARE_APPLICATION_PROTOCOLC;

    alter table SOFTWARE_APPLICATION 
        drop constraint SOFTWARE_APPLICATIONIFKC;

    alter table SOFTWARE_APP_ENDURANT 
        drop constraint SOFTWARE_APP_ENDURANTIFKC;

    alter table SOURCE_MATERIALS 
        drop constraint MATERIAL_INVESTIGATIONS_FKC;

    alter table SOURCE_MATERIALS 
        drop constraint INVESTIGATION_SOURCE_MATERIALC;

    alter table SUMMARY_RESULTS 
        drop constraint HIGHER_LEVEL_ANALYSIS_INVESTIC;

    alter table SUMMARY_RESULTS 
        drop constraint INVESTIGATION_SUMMARY_RESULTSC;

    alter table SUPPORTING_DATA 
        drop constraint HIGHER_LEVEL_ANALYSIS_SUPPORTC;

    alter table SUPPORTING_DATA 
        drop constraint DATA_HIGHER_LEVEL_ANALYSES_FKC;

    alter table URI 
        drop constraint URIIFKC;

    alter table USED_SOFTWARE 
        drop constraint SOFTWARE_PROTOCOLS_FKC;

    alter table USED_SOFTWARE 
        drop constraint PROTOCOL_USED_SOFTWARE_FKC;

    drop table ACTION;

    drop table ACTION_APPLICATION;

    drop table AFFILIATIONS;

    drop table ALL_BIBLIOGRAPHIC_REFERENCES;

    drop table ALL_CONTACTS;

    drop table ALL_DATA;

    drop table ALL_DATA_PARTITIONS;

    drop table ALL_EQUIPMENT;

    drop table ALL_PROTOCOL_APPLICATIONS;

    drop table ALL_PROTOCOL_APPS;

    drop table ALL_SEQUENCE_ANNOTATIONS;

    drop table ALL_SOFTWARE;

    drop table ANNOTATIONS;

    drop table ATOMIC_PARAMETER_VALUE;

    drop table ATOMIC_VALUE;

    drop table AUDIT;

    drop table AUDIT_COLLECTION;

    drop table BIBLIOGRAPHIC_REFERENCE;

    drop table BIBLIOGRAPHIC_REFERENCES;

    drop table BIB_REF_ENDURANT;

    drop table BOOLEAN_PARAMETER_VALUE;

    drop table BOOLEAN_VALUE;

    drop table CHARACTERISTICS;

    drop table COMPLEX_PARAMETER_VALUE;

    drop table COMPLEX_VALUE;

    drop table COMPONENTS;

    drop table COMPONENT_DESIGN_TYPES;

    drop table CONCEPTUAL_MOLECULE;

    drop table CONCEP_MOLS;

    drop table CONC_MOLECULE_COLLECTION;

    drop table CONTACT;

    drop table CONTACT_ROLE;

    drop table DATA;

    drop table DATABASE;

    drop table DATABASE_ENDURANT;

    drop table DATABASE_ENTRY;

    drop table DATA_COLLECTION;

    drop table DATA_DIMENSION;

    drop table DATA_PARTITION;

    drop table DATA_PARTITIONS;

    drop table DATA_PART_ENDURANT;

    drop table DATA_PROPERTY;

    drop table DATA_PROPERTY_ENDURANT;

    drop table DEFAULT_VALUE;

    drop table DESCRIBABLE;

    drop table DESCRIPTION;

    drop table DIMENSION;

    drop table DIMENSION_ELEMENT;

    drop table DIMENSION_ELEMENT_SET;

    drop table DIMENSION_ENDURANT;

    drop table DIM_ELEM_ENDURANT;

    drop table ENDURANT;

    drop table EQUIPMENT;

    drop table EQUIPMENT_APPLICATION;

    drop table EQUIPMENT_PARTS;

    drop table EQUIP_APP_ENDURANT;

    drop table EXTERNAL_DATA;

    drop table EXTERNAL_DATA_ENDURANT;

    drop table FACTOR;

    drop table FACTORS;

    drop table FACTOR_COLLECTION;

    drop table FACTOR_ENDURANT;

    drop table FACTOR_VALUE;

    drop table FU_G_E;

    drop table FU_G_E_ENDURANT;

    drop table GENERIC_ACTION;

    drop table GENERIC_ACTION_ENDURANT;

    drop table GENERIC_EQUIPMENT;

    drop table GENERIC_INPUT_DATA;

    drop table GENERIC_MATERIAL;

    drop table GENERIC_MATERIAL_ENDURANT;

    drop table GENERIC_MAT_MEAS;

    drop table GENERIC_PARAMETER;

    drop table GENERIC_PROTOCOL;

    drop table GENERIC_PROTOCOL_APPLICATION;

    drop table GENERIC_SOFTWARE;

    drop table GEN_EQUIP_ENDURANT;

    drop table GEN_EQUIP_PARTS;

    drop table GEN_EQ_TO_SOFT;

    drop table GEN_PARAM_ENDURANT;

    drop table GEN_PROTOCOL_ENDURANT;

    drop table GEN_PRTCL_APP_ENDURANT;

    drop table GEN_PRTCL_TO_EQUIP;

    drop table GEN_SOFTWARE;

    drop table GEN_SOFTWARE_ENDURANT;

    drop table HIGHER_LEVEL_ANALYSES;

    drop table HIGHER_LEVEL_ANALYSIS;

    drop table IDENTIFIABLE;

    drop table INPUT_DATA;

    drop table INPUT_PARTITIONS;

    drop table INTERNAL_DATA;

    drop table INTERNAL_DATA_ENDURANT;

    drop table INVESTIGATION;

    drop table INVESTIGATIONS;

    drop table INVESTIGATION_COLLECTION;

    drop table INVESTIGATION_COMPONENT;

    drop table INVESTIGATION_ENDURANT;

    drop table INVESTIGATION_TYPES;

    drop table INV_COMPONENT_ENDURANT;

    drop table MATERIAL;

    drop table MATERIALS;

    drop table MATERIAL_COLLECTION;

    drop table MATERIAL_MEASUREMENT;

    drop table MEMBERS;

    drop table NAME_VALUE_TYPE;

    drop table OBJECT_PROPERTY;

    drop table OBJ_PROPERTY_ENDURANT;

    drop table ONTOLOGY_COLLECTION;

    drop table ONTOLOGY_INDIVIDUAL;

    drop table ONTOLOGY_PROPERTY;

    drop table ONTOLOGY_SOURCE;

    drop table ONTOLOGY_SOURCES;

    drop table ONTOLOGY_TERM;

    drop table ONTOLOGY_TERMS;

    drop table ONTO_INDV_ENDURANT;

    drop table ONTO_SOURCE_ENDURANT;

    drop table ORGANIZATION;

    drop table ORGANIZATION_ENDURANT;

    drop table OUTPUT_PARTITIONS;

    drop table OWNER;

    drop table PARAMETER;

    drop table PARAMETERIZABLE;

    drop table PARAMETERIZABLE_APPLICATION;

    drop table PARAMETERIZABLE_TYPES;

    drop table PARAMETER_VALUE;

    drop table PARTITION_PAIR;

    drop table PERSON;

    drop table PERSON_ENDURANT;

    drop table PROTOCOL;

    drop table PROTOCOLS;

    drop table PROTOCOL_APPLICATION;

    drop table PROTOCOL_COLLECTION;

    drop table PROTOCOL_EQUIPMENT;

    drop table PROTOCOL_INPUT_TYPES;

    drop table PROTOCOL_OUTPUT_TYPES;

    drop table PROVIDER;

    drop table PROVIDER_ENDURANT;

    drop table QUALITY_CONTROL_STATISTICS;

    drop table RANGE_PARAMETER_VALUE;

    drop table RANGE_VALUE;

    drop table RC_TO_DATABASE;

    drop table REFERENCEABLE_COLLECTION;

    drop table SECURITY;

    drop table SECURITY_ACCESS;

    drop table SECURITY_COLLECTION;

    drop table SECURITY_ENDURANT;

    drop table SECURITY_GROUP;

    drop table SECURITY_GROUPS;

    drop table SEC_GROUP_ENDURANT;

    drop table SEQUENCE;

    drop table SEQUENCE_ANNOTATION;

    drop table SEQUENCE_ENDURANT;

    drop table SEQ_ANNOT_ENDURANT;

    drop table SOFTWARE;

    drop table SOFTWARE2_EQUIPMENT;

    drop table SOFTWARE_APPLICATION;

    drop table SOFTWARE_APP_ENDURANT;

    drop table SOURCE_MATERIALS;

    drop table SUMMARY_RESULTS;

    drop table SUPPORTING_DATA;

    drop table URI;

    drop table USED_SOFTWARE;

    drop sequence hibernate_sequence;
