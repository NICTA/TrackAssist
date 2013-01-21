-- Data locations
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'application-path','FOLDER_CONTAINING_EXECUTABLE');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'experiments-path','FOLDER_FOR_PROGRAM_DATA');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'art','art');

-- USERS / GROUPS
INSERT INTO public.ct_groups (name) VALUES ('default'); -- 1
INSERT INTO public.ct_users (name,password,firstname,lastname) VALUES ('admin','admin','n/a','n/a'); -- 1
INSERT INTO public.ct_users_groups (fk_user,fk_group) VALUES (1,1); -- 1


