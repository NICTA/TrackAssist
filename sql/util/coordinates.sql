-- This query does a select from an N-dimensional set of images given fixed 
-- points in (N-1) dimensions and allowing a range in 1 dimension. By counting
-- the number of matching coordinates associated with each image, we should be
-- able to retrieve the images with a single tablescan. Then, we need a 2nd 
-- scan from the results, filtered at the first opportunity, to add the order
-- in which the images should be displayed/used. We optionally can filter by
-- experiment (ie reducing the set of data involved).
select i2.*, c.value as ordering 
from 
(select fk_image, uri
from ct_images_coordinates 
inner join ct_images on ct_images_coordinates.fk_image = ct_images.pk_image
inner join ct_coordinates on ct_images_coordinates.fk_coordinate = ct_coordinates.pk_coordinate
where ct_images.fk_experiment = 2 -- filter parameter
and (    ( ( ct_coordinates.fk_coordinate_type = 1 ) and ( ct_coordinates.value = 1 ) ) --x this is a specified position in a dimension
      or ( ( ct_coordinates.fk_coordinate_type = 2 ) and ( ct_coordinates.value = 1 ) ) --y this is a specified position in a dimension
      or ( ( ct_coordinates.fk_coordinate_type = 3 ) and ( ct_coordinates.value = 1 ) ) --z this is a specified position in a dimension
      or ( ( ct_coordinates.fk_coordinate_type = 4 ) and ( ct_coordinates.value = 2 ) ) --c this is a specified position in a dimension
      or ( ( ct_coordinates.fk_coordinate_type = 5 ) ) -- this is the variable dimension/range of the sequence
    ) -- each line programmatically added from the axes defined
group by fk_image, uri
having count( * ) > 4 ) as i -- because (5-1)=4 dimensions fixed, 1 variable
inner join ct_images_coordinates ic on i.fk_image = ic.fk_image
inner join ct_coordinates c on ic.fk_coordinate = c.pk_coordinate
inner join ct_images i2 on i.fk_image = i2.pk_image
where c.fk_coordinate_type = 5 -- this is the dimension in which we are varying
order by c.value ASC