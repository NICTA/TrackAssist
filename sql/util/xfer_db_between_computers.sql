-- nb: to restore, select "clean before restore" option on 2nd tab in pgadmin
update ct_properties p 
set value='C:/Users/davidjr/Documents/NetbeansProjects/CellTracking' 
where p.name = 'application-path';

update ct_properties p 
set value='C:/Users/davidjr/Documents/NetbeansProjects/experiments' 
where p.name = 'experiments-path';

--                                                        1         2
--                                               12345678901234567890123456789
-- convert from :                               "D:/TrackAssistExperiments\fucci_p9\20111118-0026_Position(9)_p000001t00000001z001c01.tif"
--         to   : "C:\Users\davidjr\Documents\NetbeansProjects\experiments\fucci_p9\20111118-0026_Position(9)_p000001t00000001z001c01.tif"
update ct_images i 
set value='C:/Users/davidjr/Documents/NetbeansProjects/experiments/' || substring( value, 27 ) 
where value like 'D:%'