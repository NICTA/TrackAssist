
-- COORDINATES TYPES
INSERT INTO public.ct_coordinates_types (name) VALUES ('x'); -- 1
INSERT INTO public.ct_coordinates_types (name) VALUES ('y'); -- 2 
INSERT INTO public.ct_coordinates_types (name) VALUES ('z'); -- 3
INSERT INTO public.ct_coordinates_types (name) VALUES ('channel'); --4
INSERT INTO public.ct_coordinates_types (name) VALUES ('time'); --5

-- SYSTEM PROPERTIES
INSERT INTO public.ct_properties_types (name) VALUES ('system');
INSERT INTO public.ct_properties_types (name) VALUES ('parameter');

-- PAGE GRAPH
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'default-page','select-experiment');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'error-page','error');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'help-page','help');

INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'error,ok','select-experiment');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'login,ok','select-experiment');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'login,ng','login');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'logout,ok','login');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'select-experiment,display','select-solution');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'select-experiment,create','import-images');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'select-experiment,remove','error');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'import-images,next','parse-filenames');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'import-images,back','select-experiment');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'import-images,cancel','select-experiment');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'parse-filenames,next','create-experiment');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'parse-filenames,back','import-images');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'parse-filenames,cancel','select-experiment');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'create-experiment,finish','select-solution');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'create-experiment,back','parse-filenames');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'create-experiment,cancel','select-experiment');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'select-solution,display','display-solution');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'select-solution,create','select-solution');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'select-solution,remove','error');

INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'lineage-page','lineage');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'export-page','export');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'tracking-page','display-experiment');

INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'toolbar-max-width-pixels','120');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'toolbar-max-height-pixels','240');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'table-editor-table-names','CtProperties,CtAnnotationsTypes,CtCoordinatesTypes,CtEntityPropertiesTypes');

INSERT INTO public.ct_entity_properties_types( entity_name,name,type) VALUES ('CtDetections','id','id');
INSERT INTO public.ct_entity_properties_types( entity_name,name,type) VALUES ('CtDetections','parent','id');

INSERT INTO public.ct_annotations_types(value) VALUES ('division');
INSERT INTO public.ct_annotations_types(value) VALUES ('death');
INSERT INTO public.ct_annotations_types(value) VALUES ('note');

INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (2,'radius-CtAnnotationsTypes-1','10');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (2,'radius-CtAnnotationsTypes-2','8');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (2,'radius-CtAnnotationsTypes-3','6');

INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (2,'normal-color-argb-hex-CtAnnotationsTypes-1','7FFF0000');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (2,'normal-color-argb-hex-CtAnnotationsTypes-2','7F72AA12');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (2,'normal-color-argb-hex-CtAnnotationsTypes-3','7F866DA9');







