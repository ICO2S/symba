
    create table ACTION (
        ID BIGINT not null,
        ACTION_ORDINAL INTEGER,
        PROTOCOL_REFERENCE_FK BIGINT unique,
        PROTOCOL_FK BIGINT,
        primary key (ID)
    );

    create table ACTION_APPLICATION (
        ID BIGINT not null,
        ACTION_FK BIGINT not null,
        ACTION_DEVIATION_FK BIGINT unique,
        PROT_APP_REF_FK BIGINT unique,
        PROTOCOL_APPLICATION_FK BIGINT,
        primary key (ID)
    );

    create table AFFILIATIONS (
        PEOPLE_FK BIGINT not null,
        AFFILIATIONS_FK BIGINT not null,
        primary key (PEOPLE_FK, AFFILIATIONS_FK)
    );

    create table ALL_BIBLIOGRAPHIC_REFERENCES (
        REFERENCEABLE_COLLECTIONS_FK BIGINT not null,
        ALL_BIBLIOGRAPHIC_REFERENCE_FK BIGINT not null,
        primary key (REFERENCEABLE_COLLECTIONS_FK, ALL_BIBLIOGRAPHIC_REFERENCE_FK)
    );

    create table ALL_CONTACTS (
        AUDIT_COLLECTIONS_FK BIGINT not null,
        ALL_CONTACTS_FK BIGINT not null,
        primary key (AUDIT_COLLECTIONS_FK, ALL_CONTACTS_FK)
    );

    create table ALL_DATA (
        DATA_COLLECTIONS_FK BIGINT not null,
        ALL_DATA_FK BIGINT not null,
        primary key (DATA_COLLECTIONS_FK, ALL_DATA_FK)
    );

    create table ALL_DATA_PARTITIONS (
        DATA_COLLECTIONS_FK BIGINT not null,
        ALL_DATA_PARTITIONS_FK BIGINT not null,
        primary key (DATA_COLLECTIONS_FK, ALL_DATA_PARTITIONS_FK)
    );

    create table ALL_EQUIPMENT (
        PROTOCOL_COLLECTIONS_FK BIGINT not null,
        ALL_EQUIPMENT_FK BIGINT not null,
        primary key (PROTOCOL_COLLECTIONS_FK, ALL_EQUIPMENT_FK)
    );

    create table ALL_PROTOCOL_APPLICATIONS (
        INVESTIGATION_COMPONENTS_FK BIGINT not null,
        ALL_PROTOCOL_APPLICATIONS_FK BIGINT not null,
        primary key (INVESTIGATION_COMPONENTS_FK, ALL_PROTOCOL_APPLICATIONS_FK)
    );

    create table ALL_PROTOCOL_APPS (
        PROTOCOL_COLLECTIONS_FK BIGINT not null,
        ALL_PROTOCOL_APPS_FK BIGINT not null,
        primary key (PROTOCOL_COLLECTIONS_FK, ALL_PROTOCOL_APPS_FK)
    );

    create table ALL_SEQUENCE_ANNOTATIONS (
        CONC_MOLECULE_COLLECTIONS_FK BIGINT not null,
        ALL_SEQUENCE_ANNOTATIONS_FK BIGINT not null,
        primary key (CONC_MOLECULE_COLLECTIONS_FK, ALL_SEQUENCE_ANNOTATIONS_FK)
    );

    create table ALL_SOFTWARE (
        PROTOCOL_COLLECTIONS_FK BIGINT not null,
        ALL_SOFTWARES_FK BIGINT not null,
        primary key (PROTOCOL_COLLECTIONS_FK, ALL_SOFTWARES_FK)
    );

    create table ANNOTATIONS (
        DESCRIBABLES_FK BIGINT not null,
        ANNOTATIONS_FK BIGINT not null,
        primary key (DESCRIBABLES_FK, ANNOTATIONS_FK)
    );

    create table ATOMIC_PARAMETER_VALUE (
        ID BIGINT not null,
        VALUE CHARACTER VARYING(1024) not null,
        primary key (ID)
    );

    create table ATOMIC_VALUE (
        ID BIGINT not null,
        DEFAULT_VALUE CHARACTER VARYING(1024) not null,
        primary key (ID)
    );

    create table AUDIT (
        ID BIGINT not null,
        DATE TIMESTAMP WITH TIME ZONE not null,
        ACTION CHARACTER VARYING(1024) not null,
        PERFORMER_FK BIGINT,
        DESCRIBABLE_FK BIGINT,
        primary key (ID)
    );

    create table AUDIT_COLLECTION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table BIBLIOGRAPHIC_REFERENCE (
        ID BIGINT not null,
        AUTHORS CHARACTER VARYING(1024),
        PUBLICATION CHARACTER VARYING(1024),
        PUBLISHER CHARACTER VARYING(1024),
        EDITOR CHARACTER VARYING(1024),
        YEAR INTEGER,
        VOLUME CHARACTER VARYING(1024),
        ISSUE CHARACTER VARYING(1024),
        PAGES CHARACTER VARYING(1024),
        TITLE CHARACTER VARYING(1024),
        primary key (ID)
    );

    create table BIBLIOGRAPHIC_REFERENCES (
        IDENTIFIABLES_FK BIGINT not null,
        BIBLIOGRAPHIC_REFERENCES_FK BIGINT not null,
        primary key (IDENTIFIABLES_FK, BIBLIOGRAPHIC_REFERENCES_FK)
    );

    create table BIB_REF_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table BOOLEAN_PARAMETER_VALUE (
        ID BIGINT not null,
        VALUE BOOLEAN not null,
        primary key (ID)
    );

    create table BOOLEAN_VALUE (
        ID BIGINT not null,
        DEFAULT_VALUE BOOLEAN not null,
        primary key (ID)
    );

    create table CHARACTERISTICS (
        MATERIALS_FK BIGINT not null,
        CHARACTERISTICS_FK BIGINT not null,
        primary key (MATERIALS_FK, CHARACTERISTICS_FK)
    );

    create table COMPLEX_PARAMETER_VALUE (
        ID BIGINT not null,
        PARAMETER_VALUE_FK BIGINT not null,
        primary key (ID)
    );

    create table COMPLEX_VALUE (
        ID BIGINT not null,
        _DEFAULT_VALUE_FK BIGINT not null,
        primary key (ID)
    );

    create table COMPONENTS (
        MATERIALS_FK BIGINT not null,
        COMPONENTS_FK BIGINT not null,
        GENERIC_MATERIALS_FK BIGINT not null,
        primary key (GENERIC_MATERIALS_FK, COMPONENTS_FK)
    );

    create table COMPONENT_DESIGN_TYPES (
        INVESTIGATION_COMPONENTS_FK BIGINT not null,
        COMPONENT_DESIGN_TYPES_FK BIGINT not null,
        primary key (INVESTIGATION_COMPONENTS_FK, COMPONENT_DESIGN_TYPES_FK)
    );

    create table CONCEPTUAL_MOLECULE (
        ID BIGINT not null,
        primary key (ID)
    );

    create table CONCEP_MOLS (
        CONC_MOLECULE_COLLECTIONS_FK BIGINT not null,
        CONCEPT_MOLS_FK BIGINT not null,
        primary key (CONC_MOLECULE_COLLECTIONS_FK, CONCEPT_MOLS_FK)
    );

    create table CONC_MOLECULE_COLLECTION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table CONTACT (
        ID BIGINT not null,
        ADDRESS CHARACTER VARYING(1024),
        PHONE CHARACTER VARYING(1024),
        EMAIL CHARACTER VARYING(1024),
        FAX CHARACTER VARYING(1024),
        TOLL_FREE_PHONE CHARACTER VARYING(1024),
        primary key (ID)
    );

    create table CONTACT_ROLE (
        ID BIGINT not null,
        CONTACT_FK BIGINT not null,
        ROLE_FK BIGINT not null,
        INVESTIGATION_FK BIGINT,
        MATERIAL_FK BIGINT,
        DATABASE_FK BIGINT,
        PARAMETERIZABLE_FK BIGINT,
        PROTOCOL_APPLICATION_FK BIGINT,
        primary key (ID)
    );

    create table DATA (
        ID BIGINT not null,
        PROTOCOL_APPLICATION_FK BIGINT,
        GENERIC_PROTOCOL_APPLICATIO_FK BIGINT,
        primary key (ID)
    );

    create table DATABASE (
        ID BIGINT not null,
        VERSION CHARACTER VARYING(1024),
        U_R_I CHARACTER VARYING(1024),
        primary key (ID)
    );

    create table DATABASE_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table DATABASE_ENTRY (
        ID BIGINT not null,
        ACCESSION CHARACTER VARYING(1024) not null,
        ACCESSION_VERSION CHARACTER VARYING(1024),
        DATABASE_FK BIGINT not null,
        IDENTIFIABLE_FK BIGINT,
        primary key (ID)
    );

    create table DATA_COLLECTION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table DATA_DIMENSION (
        DATAS_FK BIGINT not null,
        DATA_DIMENSION_FK BIGINT not null,
        DATA_DATA_DIMENSION_IDX int4 not null,
        primary key (DATAS_FK, DATA_DATA_DIMENSION_IDX)
    );

    create table DATA_PARTITION (
        ID BIGINT not null,
        PARTITIONED_DATA_FK BIGINT not null,
        primary key (ID)
    );

    create table DATA_PARTITIONS (
        FACTOR_VALUES_FK BIGINT not null,
        DATA_PARTITIONS_FK BIGINT not null,
        primary key (FACTOR_VALUES_FK, DATA_PARTITIONS_FK)
    );

    create table DATA_PART_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table DATA_PROPERTY (
        ID BIGINT not null,
        DATA_TYPE CHARACTER VARYING(1024),
        VALUE CHARACTER VARYING(1024),
        primary key (ID)
    );

    create table DATA_PROPERTY_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table DEFAULT_VALUE (
        ID BIGINT not null,
        primary key (ID)
    );

    create table DESCRIBABLE (
        ID BIGINT not null,
        SECURITY_FK BIGINT,
        EXTURI_FK BIGINT unique,
        primary key (ID)
    );

    create table DESCRIPTION (
        ID BIGINT not null,
        TEXT CHARACTER VARYING(1024),
        DESCRIBABLE_FK BIGINT,
        primary key (ID)
    );

    create table DIMENSION (
        ID BIGINT not null,
        DIMENSION_TYPE_FK BIGINT,
        DATA_COLLECTION_FK BIGINT,
        primary key (ID)
    );

    create table DIMENSION_ELEMENT (
        ID BIGINT not null,
        DIMENSION_FK BIGINT,
        DIMENSION_DIMENSION_ELEMENTS_IDX int4,
        primary key (ID)
    );

    create table DIMENSION_ELEMENT_SET (
        DATA_PARTITIONS_FK BIGINT not null,
        DIMENSION_ELEMENT_SET_FK BIGINT not null,
        primary key (DATA_PARTITIONS_FK, DIMENSION_ELEMENT_SET_FK)
    );

    create table DIMENSION_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table DIM_ELEM_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table ENDURANT (
        ID BIGINT not null,
        IDENTIFIER CHARACTER VARYING(1024) not null unique,
        primary key (ID)
    );

    create table EQUIPMENT (
        ID BIGINT not null,
        MODEL_FK BIGINT,
        MAKE_FK BIGINT,
        primary key (ID)
    );

    create table EQUIPMENT_APPLICATION (
        ID BIGINT not null,
        SERIAL_NUMBER CHARACTER VARYING(1024),
        APPLIED_EQUIPMENT_FK BIGINT not null,
        PROTOCOL_APPLICATION_FK BIGINT,
        primary key (ID)
    );

    create table EQUIPMENT_PARTS (
        EQUIPMENTS_FK BIGINT not null,
        EQUIPMENT_PARTS_FK BIGINT not null,
        primary key (EQUIPMENTS_FK, EQUIPMENT_PARTS_FK)
    );

    create table EQUIP_APP_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table EXTERNAL_DATA (
        ID BIGINT not null,
        LOCATION CHARACTER VARYING(1024) not null,
        FILE_FORMAT_FK BIGINT,
        EXTERNAL_FORMAT_DOCUMENTATI_FK BIGINT unique,
        primary key (ID)
    );

    create table EXTERNAL_DATA_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table FACTOR (
        ID BIGINT not null,
        FACTOR_CATEGORY_FK BIGINT,
        primary key (ID)
    );

    create table FACTORS (
        INVESTIGATION_COMPONENTS_FK BIGINT not null,
        FACTORS_FK BIGINT not null,
        primary key (INVESTIGATION_COMPONENTS_FK, FACTORS_FK)
    );

    create table FACTOR_COLLECTION (
        INVESTIGATION_COLLECTIONS_FK BIGINT not null,
        FACTOR_COLLECTION_FK BIGINT not null,
        primary key (INVESTIGATION_COLLECTIONS_FK, FACTOR_COLLECTION_FK)
    );

    create table FACTOR_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table FACTOR_VALUE (
        ID BIGINT not null,
        VALUE_FK BIGINT,
        FACTOR_FK BIGINT,
        primary key (ID)
    );

    create table FU_G_E (
        ID BIGINT not null,
        MATERIAL_COLLECTION_FK BIGINT unique,
        CONCEP_MOL_COLLECTION_FK BIGINT unique,
        REFERENCEABLE_COLLECTION_FK BIGINT unique,
        INVESTIGATION_COLLECTION_FK BIGINT unique,
        DATA_COLLECTION_FK BIGINT unique,
        AUDIT_COLLECTION_FK BIGINT unique,
        PROTOCOL_COLLECTION_FK BIGINT unique,
        ONTOLOGY_COLLECTION_FK BIGINT unique,
        PROVIDER_FK BIGINT,
        primary key (ID)
    );

    create table FU_G_E_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GENERIC_ACTION (
        ID BIGINT not null,
        ACTION_TEXT CHARACTER VARYING(1024),
        ACTION_TERM_FK BIGINT,
        GEN_PROTOCOL_REF_FK BIGINT,
        GENERIC_PROTOCOL_FK BIGINT,
        primary key (ID)
    );

    create table GENERIC_ACTION_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GENERIC_EQUIPMENT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GENERIC_INPUT_DATA (
        GENERIC_PROTOCOL_APPLICATIO_FK BIGINT not null,
        GENERIC_INPUT_DATA_FK BIGINT not null,
        primary key (GENERIC_PROTOCOL_APPLICATIO_FK, GENERIC_INPUT_DATA_FK)
    );

    create table GENERIC_MATERIAL (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GENERIC_MATERIAL_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GENERIC_MAT_MEAS (
        ID BIGINT not null,
        GENERIC_MEASURE_MATERIAL_FK BIGINT not null,
        GENERIC_PROTOCOL_APPLICATIO_FK BIGINT,
        primary key (ID)
    );

    create table GENERIC_PARAMETER (
        ID BIGINT not null,
        PARAMETER_TYPE_FK BIGINT,
        GENERIC_PROTOCOL_FK BIGINT,
        GENERIC_SOFTWARE_FK BIGINT,
        GENERIC_EQUIPMENT_FK BIGINT,
        GENERIC_ACTION_FK BIGINT,
        primary key (ID)
    );

    create table GENERIC_PROTOCOL (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GENERIC_PROTOCOL_APPLICATION (
        ID BIGINT not null,
        GENERIC_PROTOCOL_FK BIGINT not null,
        primary key (ID)
    );

    create table GENERIC_SOFTWARE (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GEN_EQUIP_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GEN_EQUIP_PARTS (
        GENERIC_EQUIPMENTS_FK BIGINT not null,
        GEN_EQUIP_PARTS_FK BIGINT not null,
        primary key (GENERIC_EQUIPMENTS_FK, GEN_EQUIP_PARTS_FK)
    );

    create table GEN_EQ_TO_SOFT (
        SOFTWARE_FK BIGINT not null,
        GEN_EQUIPMENT_FK BIGINT not null,
        primary key (GEN_EQUIPMENT_FK, SOFTWARE_FK)
    );

    create table GEN_PARAM_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GEN_PROTOCOL_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GEN_PRTCL_APP_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table GEN_PRTCL_TO_EQUIP (
        GENERIC_PROTOCOLS_FK BIGINT not null,
        GEN_PRTCL_TO_EQUIP_FK BIGINT not null,
        primary key (GENERIC_PROTOCOLS_FK, GEN_PRTCL_TO_EQUIP_FK)
    );

    create table GEN_SOFTWARE (
        GENERIC_PROTOCOLS_FK BIGINT not null,
        GEN_SOFTWARE_FK BIGINT not null,
        primary key (GENERIC_PROTOCOLS_FK, GEN_SOFTWARE_FK)
    );

    create table GEN_SOFTWARE_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table HIGHER_LEVEL_ANALYSES (
        DATA_COLLECTIONS_FK BIGINT not null,
        HIGHER_LEVEL_ANALYSES_FK BIGINT not null,
        primary key (DATA_COLLECTIONS_FK, HIGHER_LEVEL_ANALYSES_FK)
    );

    create table HIGHER_LEVEL_ANALYSIS (
        ID BIGINT not null,
        HYPOTHESIS_FK BIGINT unique,
        CONCLUSION_FK BIGINT unique,
        primary key (ID)
    );

    create table IDENTIFIABLE (
        ID BIGINT not null,
        IDENTIFIER CHARACTER VARYING(1024) not null unique,
        NAME CHARACTER VARYING(1024),
        ENDURANT_FK BIGINT not null,
        primary key (ID)
    );

    create table INPUT_DATA (
        PROTOCOL_APPLICATIONS_FK BIGINT not null,
        INPUT_DATA_FK BIGINT not null,
        primary key (PROTOCOL_APPLICATIONS_FK, INPUT_DATA_FK)
    );

    create table INPUT_PARTITIONS (
        PARTITION_PAIRS_FK BIGINT not null,
        INPUT_PARTITIONS_FK BIGINT not null,
        primary key (PARTITION_PAIRS_FK, INPUT_PARTITIONS_FK)
    );

    create table INTERNAL_DATA (
        ID BIGINT not null,
        STORAGE BYTEA,
        primary key (ID)
    );

    create table INTERNAL_DATA_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table INVESTIGATION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table INVESTIGATIONS (
        INVESTIGATION_COLLECTIONS_FK BIGINT not null,
        INVESTIGATIONS_FK BIGINT not null,
        primary key (INVESTIGATION_COLLECTIONS_FK, INVESTIGATIONS_FK)
    );

    create table INVESTIGATION_COLLECTION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table INVESTIGATION_COMPONENT (
        ID BIGINT not null,
        REPLICATE_DESCRIPTION_FK BIGINT unique,
        QUALITY_CONTROL_DESCRIPTION_FK BIGINT unique,
        NORMALIZATION_DESCRIPTION_FK BIGINT unique,
        INVESTIGATION_FK BIGINT,
        primary key (ID)
    );

    create table INVESTIGATION_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table INVESTIGATION_TYPES (
        INVESTIGATIONS_FK BIGINT not null,
        INVESTIGATION_TYPES_FK BIGINT not null,
        primary key (INVESTIGATIONS_FK, INVESTIGATION_TYPES_FK)
    );

    create table INV_COMPONENT_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table MATERIAL (
        ID BIGINT not null,
        MATERIAL_TYPE_FK BIGINT,
        PROTOCOL_APPLICATION_FK BIGINT,
        GENERIC_PROTOCOL_APPLICATIO_FK BIGINT,
        primary key (ID)
    );

    create table MATERIALS (
        MATERIAL_COLLECTIONS_FK BIGINT not null,
        MATERIALS_FK BIGINT not null,
        primary key (MATERIAL_COLLECTIONS_FK, MATERIALS_FK)
    );

    create table MATERIAL_COLLECTION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table MATERIAL_MEASUREMENT (
        ID BIGINT not null,
        MEASURED_MATERIAL_FK BIGINT not null,
        MEASUREMENT_FK BIGINT,
        PROTOCOL_APPLICATION_FK BIGINT,
        primary key (ID)
    );

    create table MEMBERS (
        SECURITY_GROUPS_FK BIGINT not null,
        MEMBERS_FK BIGINT not null,
        primary key (SECURITY_GROUPS_FK, MEMBERS_FK)
    );

    create table NAME_VALUE_TYPE (
        ID BIGINT not null,
        NAME CHARACTER VARYING(1024),
        VALUE CHARACTER VARYING(1024),
        TYPE CHARACTER VARYING(1024),
        DESCRIBABLE_FK BIGINT,
        primary key (ID)
    );

    create table OBJECT_PROPERTY (
        ID BIGINT not null,
        primary key (ID)
    );

    create table OBJ_PROPERTY_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table ONTOLOGY_COLLECTION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table ONTOLOGY_INDIVIDUAL (
        ID BIGINT not null,
        OBJECT_PROPERTY_FK BIGINT,
        primary key (ID)
    );

    create table ONTOLOGY_PROPERTY (
        ID BIGINT not null,
        ONTOLOGY_INDIVIDUAL_FK BIGINT,
        primary key (ID)
    );

    create table ONTOLOGY_SOURCE (
        ID BIGINT not null,
        ONTOLOGY_U_R_I CHARACTER VARYING(1024) not null,
        primary key (ID)
    );

    create table ONTOLOGY_SOURCES (
        ONTOLOGY_COLLECTIONS_FK BIGINT not null,
        ONTOLOGY_SOURCES_FK BIGINT not null,
        primary key (ONTOLOGY_COLLECTIONS_FK, ONTOLOGY_SOURCES_FK)
    );

    create table ONTOLOGY_TERM (
        ID BIGINT not null,
        TERM CHARACTER VARYING(1024) not null,
        TERM_ACCESSION CHARACTER VARYING(1024) not null,
        ONTOLOGY_SOURCE_FK BIGINT,
        primary key (ID)
    );

    create table ONTOLOGY_TERMS (
        ONTOLOGY_COLLECTIONS_FK BIGINT not null,
        ONTOLOGY_TERMS_FK BIGINT not null,
        primary key (ONTOLOGY_COLLECTIONS_FK, ONTOLOGY_TERMS_FK)
    );

    create table ONTO_INDV_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table ONTO_SOURCE_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table ORGANIZATION (
        ID BIGINT not null,
        PARENT_FK BIGINT,
        primary key (ID)
    );

    create table ORGANIZATION_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table OUTPUT_PARTITIONS (
        PARTITION_PAIRS_FK BIGINT not null,
        OUTPUT_PARTITIONS_FK BIGINT not null,
        primary key (PARTITION_PAIRS_FK, OUTPUT_PARTITIONS_FK)
    );

    create table OWNER (
        SECURITIES_FK BIGINT not null,
        OWNER_FK BIGINT not null,
        primary key (SECURITIES_FK, OWNER_FK)
    );

    create table PARAMETER (
        ID BIGINT not null,
        DEFAULT_VALUE_FK BIGINT,
        UNIT_FK BIGINT,
        DATA_TYPE_FK BIGINT,
        PARAMETERIZABLE_FK BIGINT,
        ACTION_FK BIGINT,
        primary key (ID)
    );

    create table PARAMETERIZABLE (
        ID BIGINT not null,
        primary key (ID)
    );

    create table PARAMETERIZABLE_APPLICATION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table PARAMETERIZABLE_TYPES (
        PARAMETERIZABLES_FK BIGINT not null,
        PARAMETERIZABLE_TYPES_FK BIGINT not null,
        primary key (PARAMETERIZABLES_FK, PARAMETERIZABLE_TYPES_FK)
    );

    create table PARAMETER_VALUE (
        ID BIGINT not null,
        PARAMETER_FK BIGINT not null,
        PARAMETERIZABLE_APPLICATION_FK BIGINT,
        primary key (ID)
    );

    create table PARTITION_PAIR (
        ID BIGINT not null,
        PARTITION_PAIR_ALGORITHM_FK BIGINT unique,
        PROTOCOL_APPLICATION_FK BIGINT,
        primary key (ID)
    );

    create table PERSON (
        ID BIGINT not null,
        LAST_NAME CHARACTER VARYING(1024),
        FIRST_NAME CHARACTER VARYING(1024),
        MID_INITIALS CHARACTER VARYING(1024),
        primary key (ID)
    );

    create table PERSON_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table PROTOCOL (
        ID BIGINT not null,
        primary key (ID)
    );

    create table PROTOCOLS (
        PROTOCOL_COLLECTIONS_FK BIGINT not null,
        PROTOCOLS_FK BIGINT not null,
        primary key (PROTOCOL_COLLECTIONS_FK, PROTOCOLS_FK)
    );

    create table PROTOCOL_APPLICATION (
        ID BIGINT not null,
        ACTIVITY_DATE TIMESTAMP WITH TIME ZONE,
        DEVIATION_FK BIGINT unique,
        primary key (ID)
    );

    create table PROTOCOL_COLLECTION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table PROTOCOL_EQUIPMENT (
        PROTOCOLS_FK BIGINT not null,
        PROTOCOL_EQUIPMENT_FK BIGINT not null,
        primary key (PROTOCOLS_FK, PROTOCOL_EQUIPMENT_FK)
    );

    create table PROTOCOL_INPUT_TYPES (
        PROTOCOLS_FK BIGINT not null,
        INPUT_TYPES_FK BIGINT not null,
        primary key (PROTOCOLS_FK, INPUT_TYPES_FK)
    );

    create table PROTOCOL_OUTPUT_TYPES (
        PROTOCOLS_FK BIGINT not null,
        OUTPUT_TYPES_FK BIGINT not null,
        primary key (PROTOCOLS_FK, OUTPUT_TYPES_FK)
    );

    create table PROVIDER (
        ID BIGINT not null,
        PRODUCING_SOFTWARE_FK BIGINT,
        PROVIDER_FK BIGINT,
        primary key (ID)
    );

    create table PROVIDER_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table QUALITY_CONTROL_STATISTICS (
        MATERIALS_FK BIGINT not null,
        QUALITY_CONTROL_STATISTICS_FK BIGINT not null,
        primary key (MATERIALS_FK, QUALITY_CONTROL_STATISTICS_FK)
    );

    create table RANGE_PARAMETER_VALUE (
        ID BIGINT not null,
        LOW_VALUE CHARACTER VARYING(1024) not null,
        HIGH_VALUE CHARACTER VARYING(1024) not null,
        primary key (ID)
    );

    create table RANGE_VALUE (
        ID BIGINT not null,
        DEFAULT_LOW_VALUE CHARACTER VARYING(1024) not null,
        DEFAULT_HIGH_VALUE CHARACTER VARYING(1024) not null,
        primary key (ID)
    );

    create table RC_TO_DATABASE (
        REFERENCEABLE_COLLECTIONS_FK BIGINT not null,
        RC_TO_DATABASE_FK BIGINT not null,
        primary key (REFERENCEABLE_COLLECTIONS_FK, RC_TO_DATABASE_FK)
    );

    create table REFERENCEABLE_COLLECTION (
        ID BIGINT not null,
        primary key (ID)
    );

    create table SECURITY (
        ID BIGINT not null,
        primary key (ID)
    );

    create table SECURITY_ACCESS (
        ID BIGINT not null,
        ACCESS_GROUP_FK BIGINT not null,
        ACCESS_RIGHT_FK BIGINT,
        SECURITY_FK BIGINT,
        primary key (ID)
    );

    create table SECURITY_COLLECTION (
        AUDIT_COLLECTIONS_FK BIGINT not null,
        SECURITY_COLLECTION_FK BIGINT not null,
        primary key (AUDIT_COLLECTIONS_FK, SECURITY_COLLECTION_FK)
    );

    create table SECURITY_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table SECURITY_GROUP (
        ID BIGINT not null,
        primary key (ID)
    );

    create table SECURITY_GROUPS (
        AUDIT_COLLECTIONS_FK BIGINT not null,
        SECURITY_GROUPS_FK BIGINT not null,
        primary key (AUDIT_COLLECTIONS_FK, SECURITY_GROUPS_FK)
    );

    create table SEC_GROUP_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table SEQUENCE (
        ID BIGINT not null,
        LENGTH INTEGER,
        IS_APPROXIMATE_LENGTH INTEGER,
        IS_CIRCULAR INTEGER,
        SEQUENCE INTEGER,
        START INTEGER,
        SEQEND INTEGER,
        SEQUENCE_ANNOTATIONS_FK BIGINT,
        primary key (ID)
    );

    create table SEQUENCE_ANNOTATION (
        ID BIGINT not null,
        SPECIES_FK BIGINT,
        TYPE_FK BIGINT,
        POLYMER_TYPE_FK BIGINT,
        primary key (ID)
    );

    create table SEQUENCE_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table SEQ_ANNOT_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table SOFTWARE (
        ID BIGINT not null,
        VERSION CHARACTER VARYING(1024),
        primary key (ID)
    );

    create table SOFTWARE2_EQUIPMENT (
        SOFTWARES_FK BIGINT not null,
        EQUIPMENT_FK BIGINT not null,
        primary key (EQUIPMENT_FK, SOFTWARES_FK)
    );

    create table SOFTWARE_APPLICATION (
        ID BIGINT not null,
        APPLIED_SOFTWARE_FK BIGINT not null,
        PROTOCOL_APPLICATION_FK BIGINT,
        primary key (ID)
    );

    create table SOFTWARE_APP_ENDURANT (
        ID BIGINT not null,
        primary key (ID)
    );

    create table SOURCE_MATERIALS (
        INVESTIGATIONS_FK BIGINT not null,
        SOURCE_MATERIALS_FK BIGINT not null,
        primary key (INVESTIGATIONS_FK, SOURCE_MATERIALS_FK)
    );

    create table SUMMARY_RESULTS (
        INVESTIGATIONS_FK BIGINT not null,
        SUMMARY_RESULTS_FK BIGINT not null,
        primary key (INVESTIGATIONS_FK, SUMMARY_RESULTS_FK)
    );

    create table SUPPORTING_DATA (
        HIGHER_LEVEL_ANALYSES_FK BIGINT not null,
        SUPPORTING_DATA_FK BIGINT not null,
        primary key (HIGHER_LEVEL_ANALYSES_FK, SUPPORTING_DATA_FK)
    );

    create table URI (
        ID BIGINT not null,
        URI CHARACTER VARYING(1024) not null,
        primary key (ID)
    );

    create table USED_SOFTWARE (
        PROTOCOLS_FK BIGINT not null,
        USED_SOFTWARE_FK BIGINT not null,
        primary key (PROTOCOLS_FK, USED_SOFTWARE_FK)
    );

    alter table ACTION 
        add constraint ACTION_PROTOCOL_FKC 
        foreign key (PROTOCOL_FK) 
        references PROTOCOL;

    alter table ACTION 
        add constraint ACTION_PROTOCOL_REFERENCE_FKC 
        foreign key (PROTOCOL_REFERENCE_FK) 
        references PROTOCOL;

    alter table ACTION 
        add constraint ACTIONIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table ACTION_APPLICATION 
        add constraint ACTION_APPLICATION_PROT_APP_RC 
        foreign key (PROT_APP_REF_FK) 
        references PROTOCOL_APPLICATION;

    alter table ACTION_APPLICATION 
        add constraint ACTION_APPLICATION_ACTION_FKC 
        foreign key (ACTION_FK) 
        references ACTION;

    alter table ACTION_APPLICATION 
        add constraint ACTION_APPLICATION_ACTION_DEVC 
        foreign key (ACTION_DEVIATION_FK) 
        references DESCRIPTION;

    alter table ACTION_APPLICATION 
        add constraint ACTION_APPLICATION_PROTOCOL_AC 
        foreign key (PROTOCOL_APPLICATION_FK) 
        references PROTOCOL_APPLICATION;

    alter table ACTION_APPLICATION 
        add constraint ACTION_APPLICATIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table AFFILIATIONS 
        add constraint PERSON_AFFILIATIONS_FKC 
        foreign key (AFFILIATIONS_FK) 
        references ORGANIZATION;

    alter table AFFILIATIONS 
        add constraint ORGANIZATION_PEOPLE_FKC 
        foreign key (PEOPLE_FK) 
        references PERSON;

    alter table ALL_BIBLIOGRAPHIC_REFERENCES 
        add constraint REFERENCEABLE_COLLECTION_ALL_C 
        foreign key (ALL_BIBLIOGRAPHIC_REFERENCE_FK) 
        references BIBLIOGRAPHIC_REFERENCE;

    alter table ALL_BIBLIOGRAPHIC_REFERENCES 
        add constraint BIBLIOGRAPHIC_REFERENCE_REFERC 
        foreign key (REFERENCEABLE_COLLECTIONS_FK) 
        references REFERENCEABLE_COLLECTION;

    alter table ALL_CONTACTS 
        add constraint CONTACT_AUDIT_COLLECTIONS_FKC 
        foreign key (AUDIT_COLLECTIONS_FK) 
        references AUDIT_COLLECTION;

    alter table ALL_CONTACTS 
        add constraint AUDIT_COLLECTION_ALL_CONTACTSC 
        foreign key (ALL_CONTACTS_FK) 
        references CONTACT;

    alter table ALL_DATA 
        add constraint DATA_DATA_COLLECTIONS_FKC 
        foreign key (DATA_COLLECTIONS_FK) 
        references DATA_COLLECTION;

    alter table ALL_DATA 
        add constraint DATA_COLLECTION_ALL_DATA_FKC 
        foreign key (ALL_DATA_FK) 
        references DATA;

    alter table ALL_DATA_PARTITIONS 
        add constraint DATA_COLLECTION_ALL_DATA_PARTC 
        foreign key (ALL_DATA_PARTITIONS_FK) 
        references DATA_PARTITION;

    alter table ALL_DATA_PARTITIONS 
        add constraint DATA_PARTITION_DATA_COLLECTIOC 
        foreign key (DATA_COLLECTIONS_FK) 
        references DATA_COLLECTION;

    alter table ALL_EQUIPMENT 
        add constraint PROTOCOL_COLLECTION_ALL_EQUIPC 
        foreign key (ALL_EQUIPMENT_FK) 
        references EQUIPMENT;

    alter table ALL_EQUIPMENT 
        add constraint EQUIPMENT_PROTOCOL_COLLECTIONC 
        foreign key (PROTOCOL_COLLECTIONS_FK) 
        references PROTOCOL_COLLECTION;

    alter table ALL_PROTOCOL_APPLICATIONS 
        add constraint INVESTIGATION_COMPONENT_ALL_PC 
        foreign key (ALL_PROTOCOL_APPLICATIONS_FK) 
        references PROTOCOL_APPLICATION;

    alter table ALL_PROTOCOL_APPLICATIONS 
        add constraint PROTOCOL_APPLICATION_INVESTIGC 
        foreign key (INVESTIGATION_COMPONENTS_FK) 
        references INVESTIGATION_COMPONENT;

    alter table ALL_PROTOCOL_APPS 
        add constraint PROTOCOL_COLLECTION_ALL_PROTOC 
        foreign key (ALL_PROTOCOL_APPS_FK) 
        references PROTOCOL_APPLICATION;

    alter table ALL_PROTOCOL_APPS 
        add constraint PROTOCOL_APPLICATION_PROTOCOLC 
        foreign key (PROTOCOL_COLLECTIONS_FK) 
        references PROTOCOL_COLLECTION;

    alter table ALL_SEQUENCE_ANNOTATIONS 
        add constraint SEQUENCE_ANNOTATION_CONC_MOLEC 
        foreign key (CONC_MOLECULE_COLLECTIONS_FK) 
        references CONC_MOLECULE_COLLECTION;

    alter table ALL_SEQUENCE_ANNOTATIONS 
        add constraint CONC_MOLECULE_COLLECTION_ALL_C 
        foreign key (ALL_SEQUENCE_ANNOTATIONS_FK) 
        references SEQUENCE_ANNOTATION;

    alter table ALL_SOFTWARE 
        add constraint PROTOCOL_COLLECTION_ALL_SOFTWC 
        foreign key (ALL_SOFTWARES_FK) 
        references SOFTWARE;

    alter table ALL_SOFTWARE 
        add constraint SOFTWARE_PROTOCOL_COLLECTIONSC 
        foreign key (PROTOCOL_COLLECTIONS_FK) 
        references PROTOCOL_COLLECTION;

    alter table ANNOTATIONS 
        add constraint ONTOLOGY_TERM_DESCRIBABLES_FKC 
        foreign key (DESCRIBABLES_FK) 
        references DESCRIBABLE;

    alter table ANNOTATIONS 
        add constraint DESCRIBABLE_ANNOTATIONS_FKC 
        foreign key (ANNOTATIONS_FK) 
        references ONTOLOGY_TERM;

    alter table ATOMIC_PARAMETER_VALUE 
        add constraint ATOMIC_PARAMETER_VALUEIFKC 
        foreign key (ID) 
        references PARAMETER_VALUE;

    alter table ATOMIC_VALUE 
        add constraint ATOMIC_VALUEIFKC 
        foreign key (ID) 
        references DEFAULT_VALUE;

    alter table AUDIT 
        add constraint AUDIT_PERFORMER_FKC 
        foreign key (PERFORMER_FK) 
        references CONTACT;

    alter table AUDIT 
        add constraint AUDIT_DESCRIBABLE_FKC 
        foreign key (DESCRIBABLE_FK) 
        references DESCRIBABLE;

    alter table AUDIT 
        add constraint AUDITIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table AUDIT_COLLECTION 
        add constraint AUDIT_COLLECTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table BIBLIOGRAPHIC_REFERENCE 
        add constraint BIBLIOGRAPHIC_REFERENCEIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table BIBLIOGRAPHIC_REFERENCES 
        add constraint BIBLIOGRAPHIC_REFERENCE_IDENTC 
        foreign key (IDENTIFIABLES_FK) 
        references IDENTIFIABLE;

    alter table BIBLIOGRAPHIC_REFERENCES 
        add constraint IDENTIFIABLE_BIBLIOGRAPHIC_REC 
        foreign key (BIBLIOGRAPHIC_REFERENCES_FK) 
        references BIBLIOGRAPHIC_REFERENCE;

    alter table BIB_REF_ENDURANT 
        add constraint BIB_REF_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table BOOLEAN_PARAMETER_VALUE 
        add constraint BOOLEAN_PARAMETER_VALUEIFKC 
        foreign key (ID) 
        references PARAMETER_VALUE;

    alter table BOOLEAN_VALUE 
        add constraint BOOLEAN_VALUEIFKC 
        foreign key (ID) 
        references DEFAULT_VALUE;

    alter table CHARACTERISTICS 
        add constraint MAT_CHAR_FKC 
        foreign key (MATERIALS_FK) 
        references MATERIAL;

    alter table CHARACTERISTICS 
        add constraint MATERIAL_CHARACTERISTICS_FKC 
        foreign key (CHARACTERISTICS_FK) 
        references ONTOLOGY_TERM;

    alter table COMPLEX_PARAMETER_VALUE 
        add constraint COMPLEX_PARAMETER_VALUE_PARAMC 
        foreign key (PARAMETER_VALUE_FK) 
        references ONTOLOGY_TERM;

    alter table COMPLEX_PARAMETER_VALUE 
        add constraint COMPLEX_PARAMETER_VALUEIFKC 
        foreign key (ID) 
        references PARAMETER_VALUE;

    alter table COMPLEX_VALUE 
        add constraint COMPLEX_VALUE__DEFAULT_VALUE_C 
        foreign key (_DEFAULT_VALUE_FK) 
        references ONTOLOGY_TERM;

    alter table COMPLEX_VALUE 
        add constraint COMPLEX_VALUEIFKC 
        foreign key (ID) 
        references DEFAULT_VALUE;

    alter table COMPONENTS 
        add constraint MATERIAL_MATERIALS_FKC 
        foreign key (MATERIALS_FK) 
        references MATERIAL;

    alter table COMPONENTS 
        add constraint GENERIC_MATERIAL_GENERIC_MATEC 
        foreign key (GENERIC_MATERIALS_FK) 
        references GENERIC_MATERIAL;

    alter table COMPONENTS 
        add constraint GENERIC_MATERIAL_COMPONENTS_FC 
        foreign key (COMPONENTS_FK) 
        references GENERIC_MATERIAL;

    alter table COMPONENTS 
        add constraint MATERIAL_COMPONENTS_FKC 
        foreign key (COMPONENTS_FK) 
        references MATERIAL;

    alter table COMPONENT_DESIGN_TYPES 
        add constraint ONTOLOGY_TERM_INVESTIGATION_CC 
        foreign key (INVESTIGATION_COMPONENTS_FK) 
        references INVESTIGATION_COMPONENT;

    alter table COMPONENT_DESIGN_TYPES 
        add constraint INVESTIGATION_COMPONENT_COMPOC 
        foreign key (COMPONENT_DESIGN_TYPES_FK) 
        references ONTOLOGY_TERM;

    alter table CONCEPTUAL_MOLECULE 
        add constraint CONCEPTUAL_MOLECULEIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table CONCEP_MOLS 
        add constraint CONCEPTUAL_MOLECULE_CONC_MOLEC 
        foreign key (CONC_MOLECULE_COLLECTIONS_FK) 
        references CONC_MOLECULE_COLLECTION;

    alter table CONCEP_MOLS 
        add constraint CONC_MOLECULE_COLLECTION_CONCC 
        foreign key (CONCEPT_MOLS_FK) 
        references CONCEPTUAL_MOLECULE;

    alter table CONC_MOLECULE_COLLECTION 
        add constraint CONC_MOLECULE_COLLECTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table CONTACT 
        add constraint CONTACTIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table CONTACT_ROLE 
        add constraint CONTACT_ROLE_PARAMETERIZABLE_C 
        foreign key (PARAMETERIZABLE_FK) 
        references PARAMETERIZABLE;

    alter table CONTACT_ROLE 
        add constraint CONTACT_ROLE_ROLE_FKC 
        foreign key (ROLE_FK) 
        references ONTOLOGY_TERM;

    alter table CONTACT_ROLE 
        add constraint CONTACT_ROLE_MATERIAL_FKC 
        foreign key (MATERIAL_FK) 
        references MATERIAL;

    alter table CONTACT_ROLE 
        add constraint CONTACT_ROLE_INVESTIGATION_FKC 
        foreign key (INVESTIGATION_FK) 
        references INVESTIGATION;

    alter table CONTACT_ROLE 
        add constraint CONTACT_ROLE_PROTOCOL_APPLICAC 
        foreign key (PROTOCOL_APPLICATION_FK) 
        references PROTOCOL_APPLICATION;

    alter table CONTACT_ROLE 
        add constraint CONTACT_ROLE_DATABASE_FKC 
        foreign key (DATABASE_FK) 
        references DATABASE;

    alter table CONTACT_ROLE 
        add constraint CONTACT_ROLE_CONTACT_FKC 
        foreign key (CONTACT_FK) 
        references CONTACT;

    alter table CONTACT_ROLE 
        add constraint CONTACT_ROLEIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table DATA 
        add constraint GPA_OUTDATA_FKC 
        foreign key (GENERIC_PROTOCOL_APPLICATIO_FK) 
        references GENERIC_PROTOCOL_APPLICATION;

    alter table DATA 
        add constraint DATA_PROTOCOL_APPLICATION_FKC 
        foreign key (PROTOCOL_APPLICATION_FK) 
        references PROTOCOL_APPLICATION;

    alter table DATA 
        add constraint DATAIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table DATABASE 
        add constraint DATABASEIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table DATABASE_ENDURANT 
        add constraint DATABASE_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table DATABASE_ENTRY 
        add constraint DATABASE_ENTRY_IDENTIFIABLE_FC 
        foreign key (IDENTIFIABLE_FK) 
        references IDENTIFIABLE;

    alter table DATABASE_ENTRY 
        add constraint DATABASE_ENTRY_DATABASE_FKC 
        foreign key (DATABASE_FK) 
        references DATABASE;

    alter table DATABASE_ENTRY 
        add constraint DATABASE_ENTRYIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table DATA_COLLECTION 
        add constraint DATA_COLLECTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table DATA_DIMENSION 
        add constraint DIMENSION_DATAS_FKC 
        foreign key (DATAS_FK) 
        references DATA;

    alter table DATA_DIMENSION 
        add constraint DATA_DATA_DIMENSION_FKC 
        foreign key (DATA_DIMENSION_FK) 
        references DIMENSION;

    alter table DATA_PARTITION 
        add constraint DATA_PARTITION_PARTITIONED_DAC 
        foreign key (PARTITIONED_DATA_FK) 
        references DATA;

    alter table DATA_PARTITION 
        add constraint DATA_PARTITIONIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table DATA_PARTITIONS 
        add constraint FACTOR_VALUE_DATA_PARTITIONS_C 
        foreign key (DATA_PARTITIONS_FK) 
        references DATA_PARTITION;

    alter table DATA_PARTITIONS 
        add constraint DATA_PARTITION_FACTOR_VALUES_C 
        foreign key (FACTOR_VALUES_FK) 
        references FACTOR_VALUE;

    alter table DATA_PART_ENDURANT 
        add constraint DATA_PART_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table DATA_PROPERTY 
        add constraint DATA_PROPERTYIFKC 
        foreign key (ID) 
        references ONTOLOGY_PROPERTY;

    alter table DATA_PROPERTY_ENDURANT 
        add constraint DATA_PROPERTY_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table DEFAULT_VALUE 
        add constraint DEFAULT_VALUEIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table DESCRIBABLE 
        add constraint DESCRIBABLE_EXTURI_FKC 
        foreign key (EXTURI_FK) 
        references URI;

    alter table DESCRIBABLE 
        add constraint DESCRIBABLE_SECURITY_FKC 
        foreign key (SECURITY_FK) 
        references SECURITY;

    alter table DESCRIPTION 
        add constraint DESCRIPTION_DESCRIBABLE_FKC 
        foreign key (DESCRIBABLE_FK) 
        references DESCRIBABLE;

    alter table DESCRIPTION 
        add constraint DESCRIPTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table DIMENSION 
        add constraint DIMENSION_DATA_COLLECTION_FKC 
        foreign key (DATA_COLLECTION_FK) 
        references DATA_COLLECTION;

    alter table DIMENSION 
        add constraint DIMENSION_DIMENSION_TYPE_FKC 
        foreign key (DIMENSION_TYPE_FK) 
        references ONTOLOGY_TERM;

    alter table DIMENSION 
        add constraint DIMENSIONIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table DIMENSION_ELEMENT 
        add constraint DIMENSION_ELEMENT_DIMENSION_FC 
        foreign key (DIMENSION_FK) 
        references DIMENSION;

    alter table DIMENSION_ELEMENT 
        add constraint DIMENSION_ELEMENTIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table DIMENSION_ELEMENT_SET 
        add constraint DIMENSION_ELEMENT_DATA_PARTITC 
        foreign key (DATA_PARTITIONS_FK) 
        references DATA_PARTITION;

    alter table DIMENSION_ELEMENT_SET 
        add constraint DATA_PARTITION_DIMENSION_ELEMC 
        foreign key (DIMENSION_ELEMENT_SET_FK) 
        references DIMENSION_ELEMENT;

    alter table DIMENSION_ENDURANT 
        add constraint DIMENSION_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table DIM_ELEM_ENDURANT 
        add constraint DIM_ELEM_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table ENDURANT 
        add constraint ENDURANTIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table EQUIPMENT 
        add constraint EQUIPMENT_MODEL_FKC 
        foreign key (MODEL_FK) 
        references ONTOLOGY_TERM;

    alter table EQUIPMENT 
        add constraint EQUIPMENT_MAKE_FKC 
        foreign key (MAKE_FK) 
        references ONTOLOGY_TERM;

    alter table EQUIPMENT 
        add constraint EQUIPMENTIFKC 
        foreign key (ID) 
        references PARAMETERIZABLE;

    alter table EQUIPMENT_APPLICATION 
        add constraint EQUIPMENT_APPLICATION_APPLIEDC 
        foreign key (APPLIED_EQUIPMENT_FK) 
        references EQUIPMENT;

    alter table EQUIPMENT_APPLICATION 
        add constraint EQUIPMENT_APPLICATION_PROTOCOC 
        foreign key (PROTOCOL_APPLICATION_FK) 
        references PROTOCOL_APPLICATION;

    alter table EQUIPMENT_APPLICATION 
        add constraint EQUIPMENT_APPLICATIONIFKC 
        foreign key (ID) 
        references PARAMETERIZABLE_APPLICATION;

    alter table EQUIPMENT_PARTS 
        add constraint EQUIPMENT_EQUIPMENTS_FKC 
        foreign key (EQUIPMENTS_FK) 
        references EQUIPMENT;

    alter table EQUIPMENT_PARTS 
        add constraint EQUIPMENT_EQUIPMENT_PARTS_FKC 
        foreign key (EQUIPMENT_PARTS_FK) 
        references EQUIPMENT;

    alter table EQUIP_APP_ENDURANT 
        add constraint EQUIP_APP_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table EXTERNAL_DATA 
        add constraint EXTERNAL_DATA_EXTERNAL_FORMATC 
        foreign key (EXTERNAL_FORMAT_DOCUMENTATI_FK) 
        references URI;

    alter table EXTERNAL_DATA 
        add constraint EXTERNAL_DATA_FILE_FORMAT_FKC 
        foreign key (FILE_FORMAT_FK) 
        references ONTOLOGY_TERM;

    alter table EXTERNAL_DATA 
        add constraint EXTERNAL_DATAIFKC 
        foreign key (ID) 
        references DATA;

    alter table EXTERNAL_DATA_ENDURANT 
        add constraint EXTERNAL_DATA_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table FACTOR 
        add constraint FACTOR_FACTOR_CATEGORY_FKC 
        foreign key (FACTOR_CATEGORY_FK) 
        references ONTOLOGY_TERM;

    alter table FACTOR 
        add constraint FACTORIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table FACTORS 
        add constraint INVESTIGATION_COMPONENT_FACTOC 
        foreign key (FACTORS_FK) 
        references FACTOR;

    alter table FACTORS 
        add constraint FACTOR_INVESTIGATION_COMPONENC 
        foreign key (INVESTIGATION_COMPONENTS_FK) 
        references INVESTIGATION_COMPONENT;

    alter table FACTOR_COLLECTION 
        add constraint INVESTIGATION_COLLECTION_FACTC 
        foreign key (FACTOR_COLLECTION_FK) 
        references FACTOR;

    alter table FACTOR_COLLECTION 
        add constraint FACTOR_INVESTIGATION_COLLECTIC 
        foreign key (INVESTIGATION_COLLECTIONS_FK) 
        references INVESTIGATION_COLLECTION;

    alter table FACTOR_ENDURANT 
        add constraint FACTOR_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table FACTOR_VALUE 
        add constraint FACTOR_VALUE_VALUE_FKC 
        foreign key (VALUE_FK) 
        references ONTOLOGY_TERM;

    alter table FACTOR_VALUE 
        add constraint FACTOR_VALUE_FACTOR_FKC 
        foreign key (FACTOR_FK) 
        references FACTOR;

    alter table FACTOR_VALUE 
        add constraint FACTOR_VALUEIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table FU_G_E 
        add constraint FU_G_E_CONCEP_MOL_COLLECTION_C 
        foreign key (CONCEP_MOL_COLLECTION_FK) 
        references CONC_MOLECULE_COLLECTION;

    alter table FU_G_E 
        add constraint FU_G_E_PROVIDER_FKC 
        foreign key (PROVIDER_FK) 
        references PROVIDER;

    alter table FU_G_E 
        add constraint FU_G_E_DATA_COLLECTION_FKC 
        foreign key (DATA_COLLECTION_FK) 
        references DATA_COLLECTION;

    alter table FU_G_E 
        add constraint FU_G_E_MATERIAL_COLLECTION_FKC 
        foreign key (MATERIAL_COLLECTION_FK) 
        references MATERIAL_COLLECTION;

    alter table FU_G_E 
        add constraint FU_G_E_INVESTIGATION_COLLECTIC 
        foreign key (INVESTIGATION_COLLECTION_FK) 
        references INVESTIGATION_COLLECTION;

    alter table FU_G_E 
        add constraint FU_G_E_ONTOLOGY_COLLECTION_FKC 
        foreign key (ONTOLOGY_COLLECTION_FK) 
        references ONTOLOGY_COLLECTION;

    alter table FU_G_E 
        add constraint FU_G_E_PROTOCOL_COLLECTION_FKC 
        foreign key (PROTOCOL_COLLECTION_FK) 
        references PROTOCOL_COLLECTION;

    alter table FU_G_E 
        add constraint FU_G_E_AUDIT_COLLECTION_FKC 
        foreign key (AUDIT_COLLECTION_FK) 
        references AUDIT_COLLECTION;

    alter table FU_G_E 
        add constraint FU_G_E_REFERENCEABLE_COLLECTIC 
        foreign key (REFERENCEABLE_COLLECTION_FK) 
        references REFERENCEABLE_COLLECTION;

    alter table FU_G_E 
        add constraint FU_G_EIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table FU_G_E_ENDURANT 
        add constraint FU_G_E_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table GENERIC_ACTION 
        add constraint GENERIC_ACTION_ACTION_TERM_FKC 
        foreign key (ACTION_TERM_FK) 
        references ONTOLOGY_TERM;

    alter table GENERIC_ACTION 
        add constraint GENERIC_ACTION_GEN_PROTOCOL_RC 
        foreign key (GEN_PROTOCOL_REF_FK) 
        references PROTOCOL;

    alter table GENERIC_ACTION 
        add constraint GENERIC_ACTION_GENERIC_PROTOCC 
        foreign key (GENERIC_PROTOCOL_FK) 
        references GENERIC_PROTOCOL;

    alter table GENERIC_ACTION 
        add constraint GENERIC_ACTIONIFKC 
        foreign key (ID) 
        references ACTION;

    alter table GENERIC_ACTION_ENDURANT 
        add constraint GENERIC_ACTION_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table GENERIC_EQUIPMENT 
        add constraint GENERIC_EQUIPMENTIFKC 
        foreign key (ID) 
        references EQUIPMENT;

    alter table GENERIC_INPUT_DATA 
        add constraint GPA_INDATA_FKC 
        foreign key (GENERIC_PROTOCOL_APPLICATIO_FK) 
        references GENERIC_PROTOCOL_APPLICATION;

    alter table GENERIC_INPUT_DATA 
        add constraint DATA_INPUT_GPA_FKC 
        foreign key (GENERIC_INPUT_DATA_FK) 
        references DATA;

    alter table GENERIC_MATERIAL 
        add constraint GENERIC_MATERIALIFKC 
        foreign key (ID) 
        references MATERIAL;

    alter table GENERIC_MATERIAL_ENDURANT 
        add constraint GENERIC_MATERIAL_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table GENERIC_MAT_MEAS 
        add constraint GENERIC_MAT_MEAS_GENERIC_PROTC 
        foreign key (GENERIC_PROTOCOL_APPLICATIO_FK) 
        references GENERIC_PROTOCOL_APPLICATION;

    alter table GENERIC_MAT_MEAS 
        add constraint GENERIC_MAT_MEAS_GENERIC_MEASC 
        foreign key (GENERIC_MEASURE_MATERIAL_FK) 
        references MATERIAL;

    alter table GENERIC_MAT_MEAS 
        add constraint GENERIC_MAT_MEASIFKC 
        foreign key (ID) 
        references MATERIAL_MEASUREMENT;

    alter table GENERIC_PARAMETER 
        add constraint GENERIC_PARAMETER_GENERIC_SOFC 
        foreign key (GENERIC_SOFTWARE_FK) 
        references GENERIC_SOFTWARE;

    alter table GENERIC_PARAMETER 
        add constraint GENERIC_PARAMETER_PARAMETER_TC 
        foreign key (PARAMETER_TYPE_FK) 
        references ONTOLOGY_TERM;

    alter table GENERIC_PARAMETER 
        add constraint GENERIC_PARAMETER_GENERIC_ACTC 
        foreign key (GENERIC_ACTION_FK) 
        references GENERIC_ACTION;

    alter table GENERIC_PARAMETER 
        add constraint GENERIC_PARAMETER_GENERIC_EQUC 
        foreign key (GENERIC_EQUIPMENT_FK) 
        references GENERIC_EQUIPMENT;

    alter table GENERIC_PARAMETER 
        add constraint GENERIC_PARAMETER_GENERIC_PROC 
        foreign key (GENERIC_PROTOCOL_FK) 
        references GENERIC_PROTOCOL;

    alter table GENERIC_PARAMETER 
        add constraint GENERIC_PARAMETERIFKC 
        foreign key (ID) 
        references PARAMETER;

    alter table GENERIC_PROTOCOL 
        add constraint GENERIC_PROTOCOLIFKC 
        foreign key (ID) 
        references PROTOCOL;

    alter table GENERIC_PROTOCOL_APPLICATION 
        add constraint GENERIC_PROTOCOL_APPLICATION_C 
        foreign key (GENERIC_PROTOCOL_FK) 
        references PROTOCOL;

    alter table GENERIC_PROTOCOL_APPLICATION 
        add constraint GENERIC_PROTOCOL_APPLICATIONIFKC 
        foreign key (ID) 
        references PROTOCOL_APPLICATION;

    alter table GENERIC_SOFTWARE 
        add constraint GENERIC_SOFTWAREIFKC 
        foreign key (ID) 
        references SOFTWARE;

    alter table GEN_EQUIP_ENDURANT 
        add constraint GEN_EQUIP_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table GEN_EQUIP_PARTS 
        add constraint GENERIC_EQUIPMENT_GEN_EQUIP_PC 
        foreign key (GEN_EQUIP_PARTS_FK) 
        references GENERIC_EQUIPMENT;

    alter table GEN_EQUIP_PARTS 
        add constraint GENERIC_EQUIPMENT_GENERIC_EQUC 
        foreign key (GENERIC_EQUIPMENTS_FK) 
        references GENERIC_EQUIPMENT;

    alter table GEN_EQ_TO_SOFT 
        add constraint GENERIC_SOFTWARE_GEN_EQUIPMENC 
        foreign key (GEN_EQUIPMENT_FK) 
        references GENERIC_EQUIPMENT;

    alter table GEN_EQ_TO_SOFT 
        add constraint GENERIC_EQUIPMENT_SOFTWARE_FKC 
        foreign key (SOFTWARE_FK) 
        references GENERIC_SOFTWARE;

    alter table GEN_PARAM_ENDURANT 
        add constraint GEN_PARAM_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table GEN_PROTOCOL_ENDURANT 
        add constraint GEN_PROTOCOL_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table GEN_PRTCL_APP_ENDURANT 
        add constraint GEN_PRTCL_APP_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table GEN_PRTCL_TO_EQUIP 
        add constraint GENERIC_PROTOCOL_GEN_PRTCL_TOC 
        foreign key (GEN_PRTCL_TO_EQUIP_FK) 
        references GENERIC_EQUIPMENT;

    alter table GEN_PRTCL_TO_EQUIP 
        add constraint GENERIC_EQUIPMENT_GENERIC_PROC 
        foreign key (GENERIC_PROTOCOLS_FK) 
        references GENERIC_PROTOCOL;

    alter table GEN_SOFTWARE 
        add constraint GENERIC_PROTOCOL_GEN_SOFTWAREC 
        foreign key (GEN_SOFTWARE_FK) 
        references GENERIC_SOFTWARE;

    alter table GEN_SOFTWARE 
        add constraint GENERIC_SOFTWARE_GENERIC_PROTC 
        foreign key (GENERIC_PROTOCOLS_FK) 
        references GENERIC_PROTOCOL;

    alter table GEN_SOFTWARE_ENDURANT 
        add constraint GEN_SOFTWARE_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table HIGHER_LEVEL_ANALYSES 
        add constraint HIGHER_LEVEL_ANALYSIS_DATA_COC 
        foreign key (DATA_COLLECTIONS_FK) 
        references DATA_COLLECTION;

    alter table HIGHER_LEVEL_ANALYSES 
        add constraint DATA_COLLECTION_HIGHER_LEVEL_C 
        foreign key (HIGHER_LEVEL_ANALYSES_FK) 
        references HIGHER_LEVEL_ANALYSIS;

    alter table HIGHER_LEVEL_ANALYSIS 
        add constraint HIGHER_LEVEL_ANALYSIS_CONCLUSC 
        foreign key (CONCLUSION_FK) 
        references DESCRIPTION;

    alter table HIGHER_LEVEL_ANALYSIS 
        add constraint HIGHER_LEVEL_ANALYSIS_HYPOTHEC 
        foreign key (HYPOTHESIS_FK) 
        references DESCRIPTION;

    alter table HIGHER_LEVEL_ANALYSIS 
        add constraint HIGHER_LEVEL_ANALYSISIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table IDENTIFIABLE 
        add constraint IDENTIFIABLE_ENDURANT_FKC 
        foreign key (ENDURANT_FK) 
        references ENDURANT;

    alter table IDENTIFIABLE 
        add constraint IDENTIFIABLEIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table INPUT_DATA 
        add constraint PROTOCOL_APPLICATION_INPUT_DAC 
        foreign key (INPUT_DATA_FK) 
        references DATA;

    alter table INPUT_DATA 
        add constraint DATA_PROTOCOL_APPLICATIONS_FKC 
        foreign key (PROTOCOL_APPLICATIONS_FK) 
        references PROTOCOL_APPLICATION;

    alter table INPUT_PARTITIONS 
        add constraint PARTITION_IN_FKC 
        foreign key (PARTITION_PAIRS_FK) 
        references PARTITION_PAIR;

    alter table INPUT_PARTITIONS 
        add constraint PARTITION_PAIR_INPUT_PARTITIOC 
        foreign key (INPUT_PARTITIONS_FK) 
        references DATA_PARTITION;

    alter table INTERNAL_DATA 
        add constraint INTERNAL_DATAIFKC 
        foreign key (ID) 
        references DATA;

    alter table INTERNAL_DATA_ENDURANT 
        add constraint INTERNAL_DATA_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table INVESTIGATION 
        add constraint INVESTIGATIONIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table INVESTIGATIONS 
        add constraint INVESTIGATION_COLLECTION_INVEC 
        foreign key (INVESTIGATIONS_FK) 
        references INVESTIGATION;

    alter table INVESTIGATIONS 
        add constraint INVESTIGATION_INVESTIGATION_CC 
        foreign key (INVESTIGATION_COLLECTIONS_FK) 
        references INVESTIGATION_COLLECTION;

    alter table INVESTIGATION_COLLECTION 
        add constraint INVESTIGATION_COLLECTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table INVESTIGATION_COMPONENT 
        add constraint INVESTIGATION_COMPONENT_REPLIC 
        foreign key (REPLICATE_DESCRIPTION_FK) 
        references DESCRIPTION;

    alter table INVESTIGATION_COMPONENT 
        add constraint INVESTIGATION_COMPONENT_NORMAC 
        foreign key (NORMALIZATION_DESCRIPTION_FK) 
        references DESCRIPTION;

    alter table INVESTIGATION_COMPONENT 
        add constraint INVESTIGATION_COMPONENT_INVESC 
        foreign key (INVESTIGATION_FK) 
        references INVESTIGATION;

    alter table INVESTIGATION_COMPONENT 
        add constraint INVESTIGATION_COMPONENT_QUALIC 
        foreign key (QUALITY_CONTROL_DESCRIPTION_FK) 
        references DESCRIPTION;

    alter table INVESTIGATION_COMPONENT 
        add constraint INVESTIGATION_COMPONENTIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table INVESTIGATION_ENDURANT 
        add constraint INVESTIGATION_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table INVESTIGATION_TYPES 
        add constraint ONTOLOGY_TERM_INVESTIGATIONS_C 
        foreign key (INVESTIGATIONS_FK) 
        references INVESTIGATION;

    alter table INVESTIGATION_TYPES 
        add constraint INVESTIGATION_INVESTIGATION_TC 
        foreign key (INVESTIGATION_TYPES_FK) 
        references ONTOLOGY_TERM;

    alter table INV_COMPONENT_ENDURANT 
        add constraint INV_COMPONENT_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table MATERIAL 
        add constraint MATERIAL_GENERIC_PROTOCOL_APPC 
        foreign key (GENERIC_PROTOCOL_APPLICATIO_FK) 
        references GENERIC_PROTOCOL_APPLICATION;

    alter table MATERIAL 
        add constraint MATERIAL_MATERIAL_TYPE_FKC 
        foreign key (MATERIAL_TYPE_FK) 
        references ONTOLOGY_TERM;

    alter table MATERIAL 
        add constraint MATERIAL_PROTOCOL_APPLICATIONC 
        foreign key (PROTOCOL_APPLICATION_FK) 
        references PROTOCOL_APPLICATION;

    alter table MATERIAL 
        add constraint MATERIALIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table MATERIALS 
        add constraint MATERIAL_COLLECTION_MATERIALSC 
        foreign key (MATERIALS_FK) 
        references MATERIAL;

    alter table MATERIALS 
        add constraint MATERIAL_MATERIAL_COLLECTIONSC 
        foreign key (MATERIAL_COLLECTIONS_FK) 
        references MATERIAL_COLLECTION;

    alter table MATERIAL_COLLECTION 
        add constraint MATERIAL_COLLECTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table MATERIAL_MEASUREMENT 
        add constraint MATERIAL_MEASUREMENT_MEASUREDC 
        foreign key (MEASURED_MATERIAL_FK) 
        references MATERIAL;

    alter table MATERIAL_MEASUREMENT 
        add constraint MATERIAL_MEASUREMENT_MEASUREMC 
        foreign key (MEASUREMENT_FK) 
        references ONTOLOGY_TERM;

    alter table MATERIAL_MEASUREMENT 
        add constraint MATERIAL_MEASUREMENT_PROTOCOLC 
        foreign key (PROTOCOL_APPLICATION_FK) 
        references PROTOCOL_APPLICATION;

    alter table MATERIAL_MEASUREMENT 
        add constraint MATERIAL_MEASUREMENTIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table MEMBERS 
        add constraint SECURITY_GROUP_MEMBERS_FKC 
        foreign key (MEMBERS_FK) 
        references CONTACT;

    alter table MEMBERS 
        add constraint CONTACT_SECURITY_GROUPS_FKC 
        foreign key (SECURITY_GROUPS_FK) 
        references SECURITY_GROUP;

    alter table NAME_VALUE_TYPE 
        add constraint NAME_VALUE_TYPE_DESCRIBABLE_FC 
        foreign key (DESCRIBABLE_FK) 
        references DESCRIBABLE;

    alter table NAME_VALUE_TYPE 
        add constraint NAME_VALUE_TYPEIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table OBJECT_PROPERTY 
        add constraint OBJECT_PROPERTYIFKC 
        foreign key (ID) 
        references ONTOLOGY_PROPERTY;

    alter table OBJ_PROPERTY_ENDURANT 
        add constraint OBJ_PROPERTY_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table ONTOLOGY_COLLECTION 
        add constraint ONTOLOGY_COLLECTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table ONTOLOGY_INDIVIDUAL 
        add constraint ONTOLOGY_INDIVIDUAL_OBJECT_PRC 
        foreign key (OBJECT_PROPERTY_FK) 
        references OBJECT_PROPERTY;

    alter table ONTOLOGY_INDIVIDUAL 
        add constraint ONTOLOGY_INDIVIDUALIFKC 
        foreign key (ID) 
        references ONTOLOGY_TERM;

    alter table ONTOLOGY_PROPERTY 
        add constraint ONTOLOGY_PROPERTY_ONTOLOGY_INC 
        foreign key (ONTOLOGY_INDIVIDUAL_FK) 
        references ONTOLOGY_INDIVIDUAL;

    alter table ONTOLOGY_PROPERTY 
        add constraint ONTOLOGY_PROPERTYIFKC 
        foreign key (ID) 
        references ONTOLOGY_TERM;

    alter table ONTOLOGY_SOURCE 
        add constraint ONTOLOGY_SOURCEIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table ONTOLOGY_SOURCES 
        add constraint ONTOLOGY_SOURCE_ONTOLOGY_COLLC 
        foreign key (ONTOLOGY_COLLECTIONS_FK) 
        references ONTOLOGY_COLLECTION;

    alter table ONTOLOGY_SOURCES 
        add constraint ONTOLOGY_COLLECTION_ONTOLOGY_C 
        foreign key (ONTOLOGY_SOURCES_FK) 
        references ONTOLOGY_SOURCE;

    alter table ONTOLOGY_TERM 
        add constraint ONTOLOGY_TERM_ONTOLOGY_SOURCEC 
        foreign key (ONTOLOGY_SOURCE_FK) 
        references ONTOLOGY_SOURCE;

    alter table ONTOLOGY_TERM 
        add constraint ONTOLOGY_TERMIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table ONTOLOGY_TERMS 
        add constraint ONTOLOGY_TERM_FKC 
        foreign key (ONTOLOGY_TERMS_FK) 
        references ONTOLOGY_TERM;

    alter table ONTOLOGY_TERMS 
        add constraint ONTOLOGY_TERM_ONTOLOGY_COLLECC 
        foreign key (ONTOLOGY_COLLECTIONS_FK) 
        references ONTOLOGY_COLLECTION;

    alter table ONTO_INDV_ENDURANT 
        add constraint ONTO_INDV_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table ONTO_SOURCE_ENDURANT 
        add constraint ONTO_SOURCE_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table ORGANIZATION 
        add constraint ORGANIZATION_PARENT_FKC 
        foreign key (PARENT_FK) 
        references ORGANIZATION;

    alter table ORGANIZATION 
        add constraint ORGANIZATIONIFKC 
        foreign key (ID) 
        references CONTACT;

    alter table ORGANIZATION_ENDURANT 
        add constraint ORGANIZATION_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table OUTPUT_PARTITIONS 
        add constraint PARTITION_PAIR_OUTPUT_PARTITIC 
        foreign key (OUTPUT_PARTITIONS_FK) 
        references DATA_PARTITION;

    alter table OUTPUT_PARTITIONS 
        add constraint PARTITION_OUT_FKC 
        foreign key (PARTITION_PAIRS_FK) 
        references PARTITION_PAIR;

    alter table OWNER 
        add constraint CONTACT_SECURITIES_FKC 
        foreign key (SECURITIES_FK) 
        references SECURITY;

    alter table OWNER 
        add constraint SECURITY_OWNER_FKC 
        foreign key (OWNER_FK) 
        references CONTACT;

    alter table PARAMETER 
        add constraint PARAMETER_UNIT_FKC 
        foreign key (UNIT_FK) 
        references ONTOLOGY_TERM;

    alter table PARAMETER 
        add constraint PARAMETER_DATA_TYPE_FKC 
        foreign key (DATA_TYPE_FK) 
        references ONTOLOGY_TERM;

    alter table PARAMETER 
        add constraint PARAMETER_ACTION_FKC 
        foreign key (ACTION_FK) 
        references ACTION;

    alter table PARAMETER 
        add constraint PARAMETER_PARAMETERIZABLE_FKC 
        foreign key (PARAMETERIZABLE_FK) 
        references PARAMETERIZABLE;

    alter table PARAMETER 
        add constraint PARAMETER_DEFAULT_VALUE_FKC 
        foreign key (DEFAULT_VALUE_FK) 
        references DEFAULT_VALUE;

    alter table PARAMETER 
        add constraint PARAMETERIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table PARAMETERIZABLE 
        add constraint PARAMETERIZABLEIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table PARAMETERIZABLE_APPLICATION 
        add constraint PARAMETERIZABLE_APPLICATIONIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table PARAMETERIZABLE_TYPES 
        add constraint PARAMETERIZABLE_PARAMETERIZABC 
        foreign key (PARAMETERIZABLE_TYPES_FK) 
        references ONTOLOGY_TERM;

    alter table PARAMETERIZABLE_TYPES 
        add constraint ONTOLOGY_TERM_PARAMETERIZABLEC 
        foreign key (PARAMETERIZABLES_FK) 
        references PARAMETERIZABLE;

    alter table PARAMETER_VALUE 
        add constraint PARAMETER_VALUE_PARAMETER_FKC 
        foreign key (PARAMETER_FK) 
        references PARAMETER;

    alter table PARAMETER_VALUE 
        add constraint PARAMETER_VALUE_PARAMETERIZABC 
        foreign key (PARAMETERIZABLE_APPLICATION_FK) 
        references PARAMETERIZABLE_APPLICATION;

    alter table PARAMETER_VALUE 
        add constraint PARAMETER_VALUEIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table PARTITION_PAIR 
        add constraint PARTITION_PAIR_PARTITION_PAIRC 
        foreign key (PARTITION_PAIR_ALGORITHM_FK) 
        references DESCRIPTION;

    alter table PARTITION_PAIR 
        add constraint PARTITION_PAIR_PROTOCOL_APPLIC 
        foreign key (PROTOCOL_APPLICATION_FK) 
        references PROTOCOL_APPLICATION;

    alter table PARTITION_PAIR 
        add constraint PARTITION_PAIRIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table PERSON 
        add constraint PERSONIFKC 
        foreign key (ID) 
        references CONTACT;

    alter table PERSON_ENDURANT 
        add constraint PERSON_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table PROTOCOL 
        add constraint PROTOCOLIFKC 
        foreign key (ID) 
        references PARAMETERIZABLE;

    alter table PROTOCOLS 
        add constraint PROTOCOL_COLLECTION_PROTOCOLSC 
        foreign key (PROTOCOLS_FK) 
        references PROTOCOL;

    alter table PROTOCOLS 
        add constraint PROTOCOL_PROTOCOL_COLLECTIONSC 
        foreign key (PROTOCOL_COLLECTIONS_FK) 
        references PROTOCOL_COLLECTION;

    alter table PROTOCOL_APPLICATION 
        add constraint PROTOCOL_APPLICATION_DEVIATIOC 
        foreign key (DEVIATION_FK) 
        references DESCRIPTION;

    alter table PROTOCOL_APPLICATION 
        add constraint PROTOCOL_APPLICATIONIFKC 
        foreign key (ID) 
        references PARAMETERIZABLE_APPLICATION;

    alter table PROTOCOL_COLLECTION 
        add constraint PROTOCOL_COLLECTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table PROTOCOL_EQUIPMENT 
        add constraint EQUIPMENT_PROTOCOLS_FKC 
        foreign key (PROTOCOLS_FK) 
        references PROTOCOL;

    alter table PROTOCOL_EQUIPMENT 
        add constraint PROTOCOL_PROTOCOL_EQUIPMENT_FC 
        foreign key (PROTOCOL_EQUIPMENT_FK) 
        references EQUIPMENT;

    alter table PROTOCOL_INPUT_TYPES 
        add constraint P_INPUTTYPES_FK 
        foreign key (PROTOCOLS_FK) 
        references PROTOCOL;

    alter table PROTOCOL_INPUT_TYPES 
        add constraint PROTOCOL_INPUT_TYPES_FKC 
        foreign key (INPUT_TYPES_FK) 
        references ONTOLOGY_TERM;

    alter table PROTOCOL_OUTPUT_TYPES 
        add constraint P_OUTPUTTYPES_FKC 
        foreign key (PROTOCOLS_FK) 
        references PROTOCOL;

    alter table PROTOCOL_OUTPUT_TYPES 
        add constraint PROTOCOL_OUTPUT_TYPES_FKC 
        foreign key (OUTPUT_TYPES_FK) 
        references ONTOLOGY_TERM;

    alter table PROVIDER 
        add constraint PROVIDER_PROVIDER_FKC 
        foreign key (PROVIDER_FK) 
        references CONTACT_ROLE;

    alter table PROVIDER 
        add constraint PROVIDER_PRODUCING_SOFTWARE_FC 
        foreign key (PRODUCING_SOFTWARE_FK) 
        references SOFTWARE;

    alter table PROVIDER 
        add constraint PROVIDERIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table PROVIDER_ENDURANT 
        add constraint PROVIDER_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table QUALITY_CONTROL_STATISTICS 
        add constraint Material_QC_FKC 
        foreign key (MATERIALS_FK) 
        references MATERIAL;

    alter table QUALITY_CONTROL_STATISTICS 
        add constraint MATERIAL_QUALITY_CONTROL_STATC 
        foreign key (QUALITY_CONTROL_STATISTICS_FK) 
        references ONTOLOGY_TERM;

    alter table RANGE_PARAMETER_VALUE 
        add constraint RANGE_PARAMETER_VALUEIFKC 
        foreign key (ID) 
        references PARAMETER_VALUE;

    alter table RANGE_VALUE 
        add constraint RANGE_VALUEIFKC 
        foreign key (ID) 
        references DEFAULT_VALUE;

    alter table RC_TO_DATABASE 
        add constraint REFERENCEABLE_COLLECTION_RC_TC 
        foreign key (RC_TO_DATABASE_FK) 
        references DATABASE;

    alter table RC_TO_DATABASE 
        add constraint DATABASE_REFERENCEABLE_COLLECC 
        foreign key (REFERENCEABLE_COLLECTIONS_FK) 
        references REFERENCEABLE_COLLECTION;

    alter table REFERENCEABLE_COLLECTION 
        add constraint REFERENCEABLE_COLLECTIONIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table SECURITY 
        add constraint SECURITYIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table SECURITY_ACCESS 
        add constraint SECURITY_ACCESS_ACCESS_RIGHT_C 
        foreign key (ACCESS_RIGHT_FK) 
        references ONTOLOGY_TERM;

    alter table SECURITY_ACCESS 
        add constraint SECURITY_ACCESS_ACCESS_GROUP_C 
        foreign key (ACCESS_GROUP_FK) 
        references SECURITY_GROUP;

    alter table SECURITY_ACCESS 
        add constraint SECURITY_ACCESS_SECURITY_FKC 
        foreign key (SECURITY_FK) 
        references SECURITY;

    alter table SECURITY_ACCESS 
        add constraint SECURITY_ACCESSIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table SECURITY_COLLECTION 
        add constraint SECURITY_AUDIT_COLLECTIONS_FKC 
        foreign key (AUDIT_COLLECTIONS_FK) 
        references AUDIT_COLLECTION;

    alter table SECURITY_COLLECTION 
        add constraint AUDIT_COLLECTION_SECURITY_COLC 
        foreign key (SECURITY_COLLECTION_FK) 
        references SECURITY;

    alter table SECURITY_ENDURANT 
        add constraint SECURITY_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table SECURITY_GROUP 
        add constraint SECURITY_GROUPIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table SECURITY_GROUPS 
        add constraint AUDIT_COLLECTION_SECURITY_GROC 
        foreign key (SECURITY_GROUPS_FK) 
        references SECURITY_GROUP;

    alter table SECURITY_GROUPS 
        add constraint SECURITY_GROUP_AUDIT_COLLECTIC 
        foreign key (AUDIT_COLLECTIONS_FK) 
        references AUDIT_COLLECTION;

    alter table SEC_GROUP_ENDURANT 
        add constraint SEC_GROUP_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table SEQUENCE 
        add constraint SEQUENCE_SEQUENCE_ANNOTATIONSC 
        foreign key (SEQUENCE_ANNOTATIONS_FK) 
        references SEQUENCE_ANNOTATION;

    alter table SEQUENCE 
        add constraint SEQUENCEIFKC 
        foreign key (ID) 
        references CONCEPTUAL_MOLECULE;

    alter table SEQUENCE_ANNOTATION 
        add constraint SEQUENCE_ANNOTATION_POLYMER_TC 
        foreign key (POLYMER_TYPE_FK) 
        references ONTOLOGY_TERM;

    alter table SEQUENCE_ANNOTATION 
        add constraint SEQUENCE_ANNOTATION_TYPE_FKC 
        foreign key (TYPE_FK) 
        references ONTOLOGY_TERM;

    alter table SEQUENCE_ANNOTATION 
        add constraint SEQUENCE_ANNOTATION_SPECIES_FC 
        foreign key (SPECIES_FK) 
        references ONTOLOGY_TERM;

    alter table SEQUENCE_ANNOTATION 
        add constraint SEQUENCE_ANNOTATIONIFKC 
        foreign key (ID) 
        references IDENTIFIABLE;

    alter table SEQUENCE_ENDURANT 
        add constraint SEQUENCE_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table SEQ_ANNOT_ENDURANT 
        add constraint SEQ_ANNOT_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table SOFTWARE 
        add constraint SOFTWAREIFKC 
        foreign key (ID) 
        references PARAMETERIZABLE;

    alter table SOFTWARE2_EQUIPMENT 
        add constraint EQUIPMENT_SOFTWARES_FKC 
        foreign key (SOFTWARES_FK) 
        references SOFTWARE;

    alter table SOFTWARE2_EQUIPMENT 
        add constraint SOFTWARE_EQUIPMENT_FKC 
        foreign key (EQUIPMENT_FK) 
        references EQUIPMENT;

    alter table SOFTWARE_APPLICATION 
        add constraint SOFTWARE_APPLICATION_APPLIED_C 
        foreign key (APPLIED_SOFTWARE_FK) 
        references SOFTWARE;

    alter table SOFTWARE_APPLICATION 
        add constraint SOFTWARE_APPLICATION_PROTOCOLC 
        foreign key (PROTOCOL_APPLICATION_FK) 
        references PROTOCOL_APPLICATION;

    alter table SOFTWARE_APPLICATION 
        add constraint SOFTWARE_APPLICATIONIFKC 
        foreign key (ID) 
        references PARAMETERIZABLE_APPLICATION;

    alter table SOFTWARE_APP_ENDURANT 
        add constraint SOFTWARE_APP_ENDURANTIFKC 
        foreign key (ID) 
        references ENDURANT;

    alter table SOURCE_MATERIALS 
        add constraint MATERIAL_INVESTIGATIONS_FKC 
        foreign key (INVESTIGATIONS_FK) 
        references INVESTIGATION;

    alter table SOURCE_MATERIALS 
        add constraint INVESTIGATION_SOURCE_MATERIALC 
        foreign key (SOURCE_MATERIALS_FK) 
        references MATERIAL;

    alter table SUMMARY_RESULTS 
        add constraint HIGHER_LEVEL_ANALYSIS_INVESTIC 
        foreign key (INVESTIGATIONS_FK) 
        references INVESTIGATION;

    alter table SUMMARY_RESULTS 
        add constraint INVESTIGATION_SUMMARY_RESULTSC 
        foreign key (SUMMARY_RESULTS_FK) 
        references HIGHER_LEVEL_ANALYSIS;

    alter table SUPPORTING_DATA 
        add constraint HIGHER_LEVEL_ANALYSIS_SUPPORTC 
        foreign key (SUPPORTING_DATA_FK) 
        references DATA;

    alter table SUPPORTING_DATA 
        add constraint DATA_HIGHER_LEVEL_ANALYSES_FKC 
        foreign key (HIGHER_LEVEL_ANALYSES_FK) 
        references HIGHER_LEVEL_ANALYSIS;

    alter table URI 
        add constraint URIIFKC 
        foreign key (ID) 
        references DESCRIBABLE;

    alter table USED_SOFTWARE 
        add constraint SOFTWARE_PROTOCOLS_FKC 
        foreign key (PROTOCOLS_FK) 
        references PROTOCOL;

    alter table USED_SOFTWARE 
        add constraint PROTOCOL_USED_SOFTWARE_FKC 
        foreign key (USED_SOFTWARE_FK) 
        references SOFTWARE;

    create sequence hibernate_sequence;
