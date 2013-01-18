-- Data locations
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'application-path','C:\Users\davidjr\Documents\midas\open-source\TrackAssist\TrackAssist');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'experiments-path','C:\Users\davidjr\Documents\midas\open-source\TrackAssist\TrackAssist\experiments');
INSERT INTO public.ct_properties( fk_property_type,name,value) VALUES (1,'data','data');

-- USERS / GROUPS
INSERT INTO public.ct_groups (name) VALUES ('default'); -- 1
INSERT INTO public.ct_users (name,password,firstname,lastname) VALUES ('admin','admin','n/a','n/a'); -- 1
INSERT INTO public.ct_users_groups (fk_user,fk_group) VALUES (1,1); -- 1


