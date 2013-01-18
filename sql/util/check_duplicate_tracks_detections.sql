--select * from ct_tracks_detections td where td.fk_detection in 
--select (
select count(*) as q, d2.fk_solution, td2.fk_detection, td2.fk_track
from ct_tracks_detections td2
inner join ct_detections d2
on d2.pk_detection = td2.fk_detection
group by d2.fk_solution, td2.fk_detection, td2.fk_track
order by q desc
--) as q --as inside
--where q > 1
