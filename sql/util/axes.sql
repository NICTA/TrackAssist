-- find and update the axes of each experiment with the coordinates of the associated images..
select *--distinct( pk_coordinate ) 
from 
(select fk_experiment, c.fk_coordinate_type as ct1,  min( c.value ) as limit1, max( c.value ) as limit2
from ct_images_coordinates ic
inner join ct_coordinates c on ic.fk_coordinate = c.pk_coordinate
inner join ct_images i on ic.fk_image = i.pk_image
where fk_experiment = 2 --- for efficiency only update specific expt
group by i.fk_experiment, fk_coordinate_type
order by i.fk_experiment, fk_coordinate_type ) as x
--inner join ct_coordinates c2 on (( c2.fk_coordinate_type = x.ct1 ) and ( c2.value = limit1 OR c2.value = limit2 ))
--inner join ct_images_coordinates ic2 on c2.pk_coordinate = ic2.fk_coordinate
--inner join ct_images i2 on ic2.fk_image = i2.pk_image
--where i2.fk_experiment = 2 --- for efficiency only update specific expt
--order by pk_coordinate, fk_coordinate_type
--order by fk_coordinate_type
