-- ****************************************************************************
-- psql -f sql -U username
-- Assume user is default or postgres
-- ****************************************************************************


-- ****************************************************************************
-- Delete DATABASE
-- ****************************************************************************
DROP DATABASE IF EXISTS ctdb;

--****************************************************************************
-- Create database
--****************************************************************************
CREATE DATABASE ctdb;

--****************************************************************************
-- Before deleting the user/role, the assinged previlges need to be revoked
--****************************************************************************
REVOKE ALL ON DATABASE ctdb FROM ctapp;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM ctapp;
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM ctapp;
REVOKE ALL ON SCHEMA public FROM ctapp;

--****************************************************************************
-- DELETE the user if exists
--****************************************************************************
DROP USER IF EXISTS ctapp;

--****************************************************************************
-- Create the user with set password
--****************************************************************************

CREATE USER ctapp WITH PASSWORD 'password';
--Suppress NOTICE level messages. Only shows warning and above. (Replace WARNING with NOTICE to revert).
ALTER USER ctapp SET client_min_messages=WARNING;
--ALTER USER ctapp WITH SUPERUSER;

--****************************************************************************
-- Rest of the operation needs to be on ctdb, therefore connect to the ctdb database
--****************************************************************************
\connect ctdb postgres localhost 5432;
-- ****************************************************************************

--****************************************************************************
-- Drop them one by one, first the Sequences
--****************************************************************************
DROP SEQUENCE IF EXISTS ct_users_sequence,
						ct_users_groups_sequence,
						ct_groups_sequence,
						ct_experiments_sequence,
						ct_groups_experiments_sequence,
						ct_coordinates_types_sequence,
						ct_coordinates_sequence,
						ct_images_sequence,
						ct_images_coordinates_sequence,
						ct_experiments_axes_sequence,
						ct_properties_sequence,
						ct_properties_types_sequence,
						ct_entity_properties_sequence,
						ct_entity_properties_types_sequence,
						ct_uwells_sequence,
						ct_solutions_sequence,
						ct_detections_sequence,
						ct_tracks_sequence,
						ct_tracks_detections_sequence,
						ct_annotations_sequence,
						ct_annotations_types_sequence,
						ct_properties_ranges_sequence,
						CASCADE;
--****************************************************************************
-- Then the Tables
--****************************************************************************
DROP TABLE IF EXISTS	ct_users,
						ct_groups,
						ct_users_groups,
						ct_experiments,
						ct_groups_experiments,
						ct_coordinates_types,
						ct_coordinates,
						ct_images,
						ct_images_coordinates,
						ct_experiments_axes,
						ct_properties_types,
						ct_images_coordinates,
						ct_experiments_axes,
						ct_experiments_images,
						ct_properties_types,
						ct_properties,
						ct_entity_properties,
						ct_entity_properties_types,
						ct_uwells,
						ct_solutions,
						ct_detections,
						ct_tracks,
						ct_tracks_detections,
						ct_annotations,
						ct_annotations_types,
						ct_properties_ranges,
						CASCADE;
--****************************************************************************
-- Lastly the schema
--****************************************************************************						
DROP SCHEMA IF EXISTS public CASCADE;

--****************************************************************************
-- Recreate the schema
--****************************************************************************
CREATE SCHEMA public;


-- ****************************************************************************
-- Create Sequences (for autonumbering of PKs)
-- ****************************************************************************


-------------------------------------------------------------------------------
-- Part 1: Administration / Ownership of projects:
-- users --< users_groups >-- groups --< groups_experiments >-- experiments
-------------------------------------------------------------------------------
CREATE SEQUENCE ct_users_sequence              START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_users_groups_sequence       START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_groups_sequence             START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_experiments_sequence        START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_groups_experiments_sequence START WITH 1 INCREMENT BY 1;


-------------------------------------------------------------------------------
-- Part 2: Specification of projects' data:
--                   uwells_types --< uwells >-- images
-- coords_types --< coords --- images_coords >-- images >-- experiments_images
--                  coords >-- exp.ts_axes >-- exp.ts   --< experiments_images
-------------------------------------------------------------------------------
CREATE SEQUENCE ct_coordinates_types_sequence  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_coordinates_sequence        START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_images_sequence             START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_images_coordinates_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_experiments_axes_sequence   START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_experiments_images_sequence START WITH 1 INCREMENT BY 1; --made it 1 expt per image.

-------------------------------------------------------------------------------
-- Part 3: Tracking / Lineage tree solutions (detections, tracks, solutions)
-- Also the uwell detection+properties
-------------------------------------------------------------------------------

CREATE SEQUENCE ct_properties_sequence            START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_properties_types_sequence      START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_properties_ranges_sequence     START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE ct_entity_properties_sequence       START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_entity_properties_types_sequence START WITH 1 INCREMENT BY 1;


--CREATE SEQUENCE ct_experiments_properties_sequence START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE ct_uwells_sequence                START WITH 1 INCREMENT BY 1;
--CREATE SEQUENCE ct_uwells_properties_sequence     START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE ct_solutions_sequence             START WITH 1 INCREMENT BY 1;
--CREATE SEQUENCE ct_solutions_properties_sequence  START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE ct_detections_sequence            START WITH 1 INCREMENT BY 1;
--CREATE SEQUENCE ct_detections_properties_sequence START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE ct_tracks_sequence                START WITH 1 INCREMENT BY 1;
--CREATE SEQUENCE ct_tracks_properties_sequence     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_tracks_detections_sequence     START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE ct_annotations_sequence		  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ct_annotations_types_sequence	  START WITH 1 INCREMENT BY 1;



-- ****************************************************************************
-- Create Tables
-- ****************************************************************************

-------------------------------------------------------------------------------
-- Part 1: Administration / Ownership of projects:
-- users --< users_groups >-- groups --< groups_experiments >-- experiments
-------------------------------------------------------------------------------
CREATE TABLE ct_users (
    pk_user integer primary key DEFAULT nextval('ct_users_sequence'),
    name character varying unique not null,
    "password" character varying,
    firstname character varying,
     lastname character varying
);

CREATE TABLE ct_groups (
    pk_group integer primary key DEFAULT nextval('ct_groups_sequence'),
    name character varying unique not null
);

CREATE TABLE ct_users_groups ( -- users are mapped to groups to allow them to participate in many groups, and one group to be mapped to many users
    pk_user_group integer primary key DEFAULT nextval('ct_users_groups_sequence'),
    fk_user  integer references ct_users(pk_user),
    fk_group integer references ct_groups(pk_group)
);

CREATE TABLE ct_experiments (
    pk_experiment integer primary key DEFAULT nextval('ct_experiments_sequence'),
    name character varying unique not null, -- name of expt, recommend date encoded in name but not mandated
    uri character varying unique not null -- this can be the path to the experiment folder, or web address of folder on server.
);

CREATE TABLE ct_groups_experiments ( -- each experiment can be viewed/modified by many groups of people
    pk_group_experiment integer primary key DEFAULT nextval('ct_groups_experiments_sequence'),
    fk_group integer references ct_groups(pk_group),
    fk_experiment integer references ct_experiments(pk_experiment)
);


-------------------------------------------------------------------------------
-- Part 2: Specification of projects' data:
--                   uwells_types --< uwells >-- images
-- coords_types --< coords --- images_coords >-- images >-- experiments_images
--                  coords >-- exp.ts_axes >-- exp.ts   --< experiments_images
-------------------------------------------------------------------------------
CREATE TABLE ct_coordinates_types (  -- e.g. x,y,z,(c)hannel,(t)ime
    pk_coordinate_type integer primary key DEFAULT nextval('ct_coordinates_types_sequence'),
    name character varying unique not null
);

CREATE TABLE ct_coordinates (  -- values ie positions in axes
    pk_coordinate integer primary key DEFAULT nextval('ct_coordinates_sequence'),
    fk_coordinate_type integer references ct_coordinates_types(pk_coordinate_type),
    "value" integer,
    name character varying null
);

CREATE TABLE ct_images (  -- the actual image data is stored on disk
    pk_image integer primary key DEFAULT nextval('ct_images_sequence'),
    fk_experiment integer references ct_experiments(pk_experiment),
    uri character varying unique not null -- this can be the path or URI/URL to the image
);

CREATE TABLE ct_images_coordinates (  -- e.g. x,y,z,(c)hannel,(t)ime
    pk_image_coordinate integer primary key DEFAULT nextval('ct_images_coordinates_sequence'),
    fk_image integer references ct_images(pk_image),
    fk_coordinate integer references ct_coordinates(pk_coordinate)
);

CREATE TABLE ct_experiments_axes (  -- inclusive limits for experiments' data
    pk_experiment_axis integer primary key DEFAULT nextval('ct_experiments_axes_sequence'),
    fk_experiment integer references ct_experiments(pk_experiment),
    fk_coordinate1 integer references ct_coordinates(pk_coordinate),
    fk_coordinate2 integer references ct_coordinates(pk_coordinate)
);

--CREATE TABLE ct_experiments_images (  -- data for a specific experiment
--    pk_experiment_image integer primary key DEFAULT nextval('ct_experiments_images_sequence'),
--    fk_experiment integer references ct_experiments(pk_experiment),
--    fk_image integer references ct_images(pk_image)
--);

CREATE TABLE ct_properties_types (  -- keyed attributes
    pk_property_type integer primary key DEFAULT nextval('ct_properties_types_sequence'),
    name character varying unique not null
);


CREATE TABLE ct_properties (  -- keyed attributes values
    pk_property integer primary key DEFAULT nextval('ct_properties_sequence'),
    fk_property_type integer references ct_properties_types(pk_property_type),
    name character varying unique not null,
    "value" character varying
);

-- define the convex hypercube within which the property applies
CREATE TABLE ct_properties_ranges ( 
    pk_property_range integer primary key DEFAULT nextval('ct_properties_ranges_sequence'),
    fk_property integer references ct_properties(pk_property),
    fk_coordinate1 integer references ct_coordinates(pk_coordinate),
    fk_coordinate2 integer references ct_coordinates(pk_coordinate)
);

CREATE TABLE ct_entity_properties_types (  -- properties tied to entities
    pk_entity_property_type integer primary key DEFAULT nextval('ct_entity_properties_types_sequence'),
    entity_name character varying not null, 
    name character varying not null,
    "type" character varying not null,
    UNIQUE (entity_name, name)
);

CREATE TABLE ct_uwells (  -- microwells, multiple chambers visible within an image.
    pk_uwell integer primary key DEFAULT nextval('ct_uwells_sequence'),
    fk_image integer references ct_images(pk_image), -- image this uwell is within
    position character varying,
    boundary character varying  -- how to define the uwell positions? As string allows applications to use different conventions
);

--CREATE TABLE ct_experiments_properties (  -- attributes of a detection, its state
--    pk_experiment_property integer primary key DEFAULT nextval('ct_experiments_properties_sequence'),
--    fk_experiment integer references ct_experiments(pk_experiment), -- where the cell was detected
--    fk_property integer references ct_properties(pk_property)
--);
--
--CREATE TABLE ct_uwells_properties (  -- attributes of a detection, its state
--    pk_uwell_property integer primary key DEFAULT nextval('ct_uwells_properties_sequence'),
--    fk_uwell integer references ct_uwells(pk_uwell), -- where the cell was detected
--    fk_property integer references ct_properties(pk_property)
--);

-------------------------------------------------------------------------------
-- Part 3: Tracking / Lineage tree solutions (detections, tracks, solutions)
--
-------------------------------------------------------------------------------
--CREATE TABLE ct_solutions (  -- all the data required to analyse activity in a uwell, or AN attempt to analyse a uwell
--    pk_solution integer primary key DEFAULT nextval('ct_solutions_sequence'),
--    fk_uwell integer references ct_uwells(pk_uwell), -- where the activity occurs
--    name character varying unique not null -- so we can keep track of it
--);
CREATE TABLE ct_solutions (  -- all the data required to analyse activity in a uwell, or AN attempt to analyse a uwell
    pk_solution integer primary key DEFAULT nextval('ct_solutions_sequence'),
    fk_experiment integer references ct_experiments(pk_experiment), -- where the activity occurs
    name character varying not null -- so we can keep track of it, the unique property is removed for now, 01/08/2011
);

--CREATE TABLE ct_solutions_properties (  -- attributes of a solutions, its state
--    pk_solution_property integer primary key DEFAULT nextval('ct_solutions_properties_sequence'),
--    fk_solution integer references ct_solutions(pk_solution), -- where the cell was detected
--    fk_property integer references ct_properties(pk_property)
--);


CREATE TABLE ct_detections (  -- detected cell in a uwell
    pk_detection integer primary key DEFAULT nextval('ct_detections_sequence'),
    fk_solution integer references ct_solutions(pk_solution), -- where the cell was detected
    fk_image integer references ct_images(pk_image), -- image this uwell is within
    location character varying,
    boundary character varying  -- how to define the detection positions? As string allows applications to use different conventions
);

--CREATE TABLE ct_detections_properties (  -- attributes of a detection, its state
--    pk_detection_property integer primary key DEFAULT nextval('ct_detections_properties_sequence'),
--    fk_detection integer references ct_detections(pk_detection), -- where the cell was detected
--    fk_property integer references ct_properties(pk_property)
--);

CREATE TABLE ct_tracks (  -- sequence of detections
    pk_track integer primary key DEFAULT nextval('ct_tracks_sequence'),
    fk_solution integer references ct_solutions(pk_solution) -- solution in which this track is present
);

--CREATE TABLE ct_tracks_properties (  -- attributes of a detection, its state
--    pk_track_property integer primary key DEFAULT nextval('ct_tracks_properties_sequence'),
--    fk_track integer references ct_tracks(pk_track),
--    fk_property integer references ct_properties(pk_property)
--);

CREATE TABLE ct_tracks_detections (  -- sequence of detections
    pk_track_detection integer primary key DEFAULT nextval('ct_tracks_detections_sequence'),
    fk_track integer references ct_tracks(pk_track),
    fk_detection integer references ct_detections(pk_detection) -- sequence of detections by ordering in time dimension
);

CREATE TABLE ct_annotations_types(
	pk_annotation_type integer primary key DEFAULT nextval('ct_annotations_types_sequence'),
	"value" character varying
);

CREATE TABLE ct_annotations (
	pk_annotation integer primary key DEFAULT nextval('ct_annotations_sequence'),
	fk_annotation_type integer references ct_annotations_types(pk_annotation_type),
	fk_solution integer references ct_solutions(pk_solution),
	fk_image integer references ct_images(pk_image),
	"value" character varying,
	x double precision,
	y double precision
);

CREATE TABLE ct_entity_properties (  -- properties tied to entities
    pk_entity_property integer primary key DEFAULT nextval('ct_entity_properties_sequence'),
	fk_solution integer references ct_solutions(pk_solution),
    entity_name character varying not null, 
    entity_pk integer,
    name character varying not null,
    value character varying not null,
    UNIQUE (entity_name, entity_pk, name)
);


--****************************************************************************
-- Finally Grant ctapp some previleges
--****************************************************************************
GRANT ALL ON DATABASE ctdb TO ctapp;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO ctapp;
GRANT ALL ON ALL TABLES IN SCHEMA public TO ctapp;
GRANT ALL ON SCHEMA public TO ctapp;














