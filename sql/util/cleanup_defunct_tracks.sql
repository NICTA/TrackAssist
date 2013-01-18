delete from ct_tracks_detections td where td.pk_track_detection in (
select pk_track_detection from ct_tracks_detections td
inner join ct_tracks t on td.fk_track = t.pk_track );
--where t.fk_solution = 11 );

--select * 
delete from ct_tracks t where t.pk_track not in (
select pk_track from ct_tracks_detections td
inner join ct_tracks t2 on td.fk_track = t2.pk_track 
 );
--and t.fk_solution = 11

delete from ct_detections d;