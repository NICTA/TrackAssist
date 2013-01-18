select * 
from ct_tracks_detections td inner join ct_detections d on td.fk_detection = d.pk_detection
where td.fk_track >= 176
order by pk_track_detection