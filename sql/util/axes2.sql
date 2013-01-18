-- find and update the axes of each experiment with the coordinates of the associated images..
select min( pk_coordinate ), value--distinct( pk_coordinate, value )
from ct_images_coordinates ic
inner join ct_coordinates c on ic.fk_coordinate = c.pk_coordinate
inner join ct_images i on ic.fk_image = i.pk_image
where fk_experiment = 2 --- for efficiency only update specific expt
and c.fk_coordinate_type = 5
and (c.value = 3 or value = 5)
group by value
order by value
--order by 
--limit 1