select * 
from ct_tracks t 
inner join ct_tracks_detections td on t.pk_track = td.fk_track
inner join ct_detections d on d.pk_detection = td.fk_detection
 where t.fk_solution = 11 
 order by fk_detection